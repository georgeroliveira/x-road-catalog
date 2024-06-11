# do not repack jars
%define __jar_repack %{nil}
%define dist %(/usr/lib/rpm/redhat/dist.sh)

Name:               xroad-catalog-collector
Version:            %{xroad_catalog_version}
Release:            %{rel}%{?snapshot}%{?dist}
Summary:            X-Road Service Listing
Group:              Applications/Internet
License:            MIT
Requires:           systemd, cronie, cronie-anacron, postgresql, postgresql-server >= 10, java-21-openjdk
Requires(post):     systemd
Requires(preun):    systemd
Requires(postun):   systemd

%define src %{_topdir}
%define jlib /usr/lib/xroad-catalog
%define conf /etc/xroad/xroad-catalog

%description
X-Road service listing

%prep

%build

%install
echo "CATALOG_PROFILE=%{profile}" > catalog-profile.properties
mkdir -p %{buildroot}%{jlib}
mkdir -p %{buildroot}%{conf}
mkdir -p %{buildroot}%{_unitdir}
mkdir -p %{buildroot}/usr/share/xroad/bin
mkdir -p %{buildroot}/usr/share/xroad/sql
mkdir -p %{buildroot}/var/log/xroad/
mkdir -p %{buildroot}/usr/share/doc/%{name}

cp -p %{src}/../../../build/libs/xroad-catalog-collector-%{version}.jar %{buildroot}%{jlib}/%{name}.jar
cp -p %{src}/../../../build/resources/main/collector-production.properties %{buildroot}%{conf}
cp -p %{src}/../../../build/resources/main/catalogdb-production.properties %{buildroot}%{conf}
cp -p catalog-profile.properties %{buildroot}%{conf}
cp -p  ../../../../../xroad-catalog-persistence/src/main/sql/init_database.sql %{buildroot}/usr/share/xroad/sql
cp -p  ../../../../../xroad-catalog-persistence/src/main/sql/create_tables_%{profile}.sql %{buildroot}/usr/share/xroad/sql
cp -p %{src}/SOURCES/%{name} %{buildroot}/usr/share/xroad/bin
cp -p %{src}/SOURCES/%{name}.service %{buildroot}%{_unitdir}
cp -p %{src}/../../../../LICENSE.txt %{buildroot}/usr/share/doc/%{name}/LICENSE.txt
cp -p %{src}/../../../../3RD-PARTY-NOTICES.txt %{buildroot}/usr/share/doc/%{name}/3RD-PARTY-NOTICES.txt

%clean
rm -rf %{buildroot}

%files
%defattr(600,xroad-catalog,xroad-catalog,-)
%config(noreplace) %{conf}/collector-production.properties
%config(noreplace) %{conf}/catalogdb-production.properties

%attr(644, xroad-catalog, xroad-catalog) %{conf}/catalogdb-production.properties
%attr(644, xroad-catalog, xroad-catalog) %{conf}/catalog-profile.properties
%attr(644,root,root) /usr/share/xroad/sql/init_database.sql
%attr(644,root,root) /usr/share/xroad/sql/create_tables_%{profile}.sql
%attr(644,root,root) %{_unitdir}/%{name}.service
%attr(755,xroad-catalog,xroad-catalog) %{jlib}/%{name}.jar
%attr(744,xroad-catalog,xroad-catalog) /usr/share/xroad/bin/%{name}

%doc /usr/share/doc/%{name}/LICENSE.txt
%doc /usr/share/doc/%{name}/3RD-PARTY-NOTICES.txt

%pre

if ! id xroad-catalog > /dev/null 2>&1 ; then
    adduser --system --no-create-home --shell /bin/false xroad-catalog
fi

%post

PGSETUP_INITDB_OPTIONS="--auth-host=md5 -E UTF8" /usr/bin/postgresql-setup initdb || return 1
systemctl start postgresql

if sudo -u postgres psql -lqt |cut -d \| -f 1 | grep -qw xroad_catalog ; then
    echo "Database already exists, creating only non-existing tables"
    sudo -u postgres psql --file=/usr/share/xroad/sql/create_tables_%{profile}.sql
else
    echo "Initializing database and creating tables"
    sudo -u postgres psql --file=/usr/share/xroad/sql/init_database.sql
    sudo -u postgres psql --file=/usr/share/xroad/sql/create_tables_%{profile}.sql
fi

%systemd_post %{name}.service

%preun
%systemd_preun %{name}.service

%postun
%systemd_postun_with_restart %{name}.service


%changelog

