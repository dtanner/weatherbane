#!/bin/sh
# new centos setup for weathervane

# do this part manually for now

vi /etc/sudoers ...  uncomment %wheel  ALL=(ALL)       NOPASSWD: ALL
# and commend the line above it 


adduser weathervane
gpasswd -a weathervane sudo
passwd weathervane
# set a password
gpasswd -a weathervane wheel
# copy public key to ~/.ssh/authorized_keys
# chmod 700 on ~/.ssh
# chmod 600 on ~/.ssh/authorized_keys

# end manual

# as root
sudo timedatectl set-local-rtc 0

sudo systemctl start firewalld
# change ssh port
sudo vi /etc/ssh/sshd_config
sudo firewall-cmd --add-port 2200/tcp --permanent
sudo firewall-cmd --add-port 2200/tcp
sudo service sshd restart
sudo firewall-cmd --reload
sudo systemctl enable firewalld

sudo yum install -y ntp
sudo systemctl start ntpd
sudo systemctl enable ntpd

sudo fallocate -l 4G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
sudo sh -c 'echo "/swapfile none swap sw 0 0" >> /etc/fstab'


cd ~
wget --no-cookies --no-check-certificate --header "Cookie: gpw_e24=http%3A%2F%2Fwww.oracle.com%2F; oraclelicense=accept-securebackup-cookie" "http://download.oracle.com/otn-pub/java/jdk/8u101-b13/jdk-8u101-linux-x64.rpm"
sudo yum localinstall -y jdk-8u101-linux-x64.rpm
rm ~/jdk-8u101-linux-x64.rpm
sudo sh -c "echo export JAVA_HOME=/usr/java/latest/jre >> /etc/environment"

sudo rpm -ivh https://download.postgresql.org/pub/repos/yum/9.5/redhat/rhel-7-x86_64/pgdg-centos95-9.5-2.noarch.rpm
sudo yum install -y postgresql95 postgresql95-server postgresql95-libs postgresql95-contrib
sudo /usr/pgsql-9.5/bin/postgresql95-setup initdb
sudo service postgresql-9.5 start
sudo chkconfig postgresql-9.5 on

# sudo vi /var/lib/pgsql/9.5/data/pg_hba.conf
# change ident to md5 for host all

sudo -u postgres -i
createuser --interactive

sudo -u postgres psql
create database weathervane;
\password weathervane


# run the create-db.sql script

# "deploy"
gw assemble
scp code/projects/weathervane/build/distributions/weathervane-0.1.0-SNAPSHOT.tar weathervane@weathervane:/tmp/weathervane.tar

# crontab
# 00 11 * * * . $HOME/.bashrc; /home/weathervane/weathervane-0.1.0-SNAPSHOT/bin/weathervane

#read console mail
cat /var/spool/mail/weathervane

# configure rsyslog
# vi /etc/rsyslog.conf: add this line:
local6.*                                                /var/log/weathervane.log
# uncomment these lines:
$ModLoad imudp
$UDPServerRun 514

sudo systemctl restart rsyslog.service
# todo - log rotation

# loggly setup:
cd
curl -O https://www.loggly.com/install/configure-file-monitoring.sh
sudo bash configure-file-monitoring.sh -a weathervane -t MY_TOKEN -u dan -f /var/log/weathervane.log -l applog


#fail2ban
sudo yum install -y epel-release
sudo yum install -y fail2ban
sudo systemctl enable fail2ban
sudo vi /etc/fail2ban/jail.local
#######
[DEFAULT]
# Ban hosts for one hour:
bantime = 3600

# Override /etc/fail2ban/jail.d/00-firewalld.conf:
banaction = iptables-multiport

[sshd]
enabled = true
#######
sudo systemctl restart fail2ban

# sophisticated deployment technique
gw clean assemble
scp /Users/dan/Dropbox/code/projects/weathervane/build/distributions/weathervane-0.1.0-SNAPSHOT.tar weathervane@weathervane:~/weathervane.tar
ssh weathervane
tar xvf weathervane.tar
