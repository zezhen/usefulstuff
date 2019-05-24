if [ -f /etc/bashrc ]; then  
    . /etc/bashrc
fi

export CLICOLOR='xterm-color'
export LSCOLORS='gxfxcxdxbxegedabagacad'

# aliases
alias ls='ls -G'
alias ll='ls -lG '
alias la='ls -alG'
alias grep='grep --color'
alias egrep='egrep --color'
alias fgrep='fgrep --color'

# grid profile
alias hdf='hadoop fs'
alias hls='hadoop fs -ls'
alias hrmr='hadoop fs -rm -r -skipTrash'
alias hcp='hadoop fs -cp'
alias hcat='hadoop fs -cat'
alias hget='hadoop fs -get'
alias hput='hadoop fs -put'
alias htext='hadoop fs -text'
alias hcount='hadoop fs -count'
alias hmv='hadoop fs -mv'
alias hmkdir='hadoop fs -mkdir -p'
alias hchmod='hadoop fs -chmod -R'
alias distcp='hadoop distcp -Dmapred.job.queue.name=curveball_med'

alias mhive='hive -hiveconf mapred.job.queue.name=curveball_med -e '
alias mkinit='kinit zezhen@Y.CORP.YAHOO.COM'

# special command
alias tconvert="awk '{\$1=strftime(\"%FT%H:%M:%S\",\$1/1000); print \$0}'"
alias msum='paste -sd+ | bc'
alias mmysql='mysql -uroot -h`hostname` -e '

alias commajointoline='xargs | sed "s/ /,/g"'

# short link
export pr='hdfs://phazonred-nn1.red.ygrid.yahoo.com:8020'
export pb='hdfs://phazonblue-nn1.blue.ygrid.yahoo.com:8020'
export jb='hdfs://jetblue-nn1.blue.ygrid.yahoo.com:8020'

function reset_oozie {
  export namenode=$(hadoop fs -df / | tail -1 | awk '{ print $1 }' | cut -d '/' -f 3 | cut -d ':' -f 1)
  export oozienode=$(echo $namenode | sed 's/nn1/oozie/g')
  export OOZIE_URL=http://$oozienode:4080/oozie
}

function moozie {
  if [ -z $OOZIE_URL ]; then
    reset_oozie
  fi
  /home/y/var/yoozieclient/bin/oozie job -auth kerberos -oozie ${OOZIE_URL} $@
}

function whereami {
  user=`whoami`
  PS1="["
  PS1="$PS1\[\e[36m\]\u\[\e[0m\]"
  PS1="$PS1@"
  if [[ `hostname` =~ 'ygrid.yahoo.com' ]]; then
    kinit -kt ~/$user.prod.headless.keytab $user@YGRID.YAHOO.COM > /dev/null 2>&1
    if [ $? -eq 1 ]; then
      alias mkinit='kinit $user@Y.CORP.YAHOO.COM'
      mkinit
    fi
    CLUSTER_NAME=$(hadoop fs -df / | tail -1 | awk '{ print $1 }' | cut -d '/' -f 3 | cut -d '-' -f 1)
    PS1="$PS1\[\e[32;40m\]$CLUSTER_NAME\[\e[0m\]"
  else
    if [ "x$YROOT_NAME" != "x" ]; then
      PS1="$PS1\[\e[32;40m\]$YROOT_NAME\[\e[0m\]"
      PS1="$PS1@"
    fi
    PS1="$PS1\[\e[34;1m\]\h\[\e[0m\]"
  fi
  PS1="$PS1 \W]"
  PS1="$PS1\\$ "
}

whereami
