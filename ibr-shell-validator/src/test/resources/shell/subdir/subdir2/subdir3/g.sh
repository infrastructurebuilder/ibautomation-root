#
# 
#
# 
# 
# 
# 
# 
# 
# 
# 
# 
# 
# 
# 
# 
# 
# 
# 
#
# 
# 
#

if [ $# -lt 1 ]
then
  echo "Usage: $0 file ..."
  exit 1
fi

echo "$0 counts the lines of code" 
l=0
n=0
s=0
for f in $*
do
  l=`wc -l $f | sed 's/^\([0-9]*\).*$/\1/'`
  echo "$f: $l"
        n=$[ $n + 1 ]
        s=$[ $s + $l ]


echo "$n files in total, with $s lines in total"