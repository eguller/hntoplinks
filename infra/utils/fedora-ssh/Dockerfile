FROM fedora:latest

RUN yum -y install openssh-server

COPY .ssh/authorized_keys /root/.ssh/authorized_keys
RUN chmod 644 /root/.ssh
RUN chmod 600 /root/.ssh/authorized_keys

RUN /usr/bin/ssh-keygen -A

CMD /usr/sbin/sshd -D

