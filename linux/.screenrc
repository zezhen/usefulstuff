#escape "``"

# Set the last line of the terminal to have a useful, pretty status line kinda like the GNOME panel
caption always "%{WK}%?%-Lw%?%{mw}%n*%f %t%?(%u)%?%{WK}%?%+Lw %= %{cK}%u@%H %{yK}%D %{cK}%M%{wK} %{cK}%d %{gK}%c"
#caption always "%{WK}%?%-Lw%?%{mw}%n*%f %t%?(%u)%?%{WK}%?%+Lw %= %u%{cK}$USER@%H"

# Use ALT-, and ALT-. to switch to previous and next window, respectively.
bindkey "^[," prev
bindkey "^[." next

# Use ALT-= and ALT-- to resize the screen sizes.
bindkey "^[=" resize +1
bindkey "^[-" resize -1

# binding the F1 - F8 keys to the first 8 screens, F11 = previous | F12 = next
bindkey -k k1 select 11
bindkey -k k2 select 12
bindkey -k k3 select 13
bindkey -k k4 select 14
bindkey -k k5 select 15
bindkey -k k6 select 16
bindkey -k k7 select 17
bindkey -k k8 select 18
bindkey -k F2 next
bindkey -k F1 prev

# syntax: screen -t label order command

screen -t kw 0
screen -t kw-udf 1
screen -t core 2
screen -t hbase_blue 3
screen -t hbase_red 4
screen -t spgen_blue 5
screen -t spgen_red 6
screen -t launcher_blue 7
screen -t launcher_red 8
screen -t cms 9
screen -t spgen_func 10
screen -t func-ws 11


shell -$SHELL

