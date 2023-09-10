# do not repack jars
%define __jar_repack %{nil}
%define dist %(/usr/lib/rpm/redhat/dist.sh)

Name:               xroad-catalog-lister
Version:            %{xroad_catalog_version}
Release:            %{rel}%{?snapshot}%{?dist}
Summary:            X-Road Service Listing
Group:              Applications/Internet
License:            MIT
Requires:           systemd, java-11-openjdk, xroad-confclient
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
mkdir -p %{buildroot}%{_unitdir}
mkdir -p %{buildroot}%{conf}
mkdir -p %{buildroot}/usr/share/xroad/bin
mkdir -p %{buildroot}/var/log/xroad/
cp -p %{src}/../../../build/libs/xroad-catalog-lister-%{version}.jar %{buildroot}%{jlib}/%{name}.jar
cp -p %{src}/SOURCES/%{name}.service %{buildroot}%{_unitdir}
cp -p %{src}/SOURCES/%{name} %{buildroot}/usr/share/xroad/bin
cp -p %{src}/../../../build/resources/main/lister-production.properties %{buildroot}%{conf}
cp -p catalog-profile.properties %{buildroot}%{conf}
cp -p %{src}/../../../build/resources/main/version.properties %{buildroot}%{conf}

%clean
rm -rf %{buildroot}

%files
%attr(644,root,root) %{_unitdir}/%{name}.service
%attr(755,xroad-catalog,xroad-catalog) %{jlib}/%{name}.jar
%attr(744,xroad-catalog,xroad-catalog) /usr/share/xroad/bin/%{name}
%config(noreplace) %{conf}/lister-production.properties
%attr(644,root,root) %{conf}/version.properties
%attr(644, xroad-catalog, xroad-catalog) %{conf}/catalog-profile.properties

%pre
if ! id xroad-catalog > /dev/null 2>&1 ; then
    adduser --system --no-create-home --shell /bin/false xroad-catalog
fi

%post -p /bin/bash
%systemd_post %{name}.service

if ! id -nG "xroad-catalog" | grep -qw "^xroad$"; then
    usermod -a -G xroad xroad-catalog
fi

%preun
%systemd_preun %{name}.service

%postun
%systemd_postun_with_restart %{name}.service

%changelog

