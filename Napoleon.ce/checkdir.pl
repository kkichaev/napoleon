#
# checkdir.pl dir
# создает каталог dir, если он не был создан заранее
# 
die "checkdir.pl dir" if $#ARGV != 0;
exit 0 if -d $ARGV[0];

# заменим / на \
$ARGV[0] =~ s-/-\\-g;

@list = split(/\\/, $ARGV[0]);
$curDir = "";
for my $cd (@list)
{
   $curDir = $curDir . "\\" if $curDir ne "";
   $curDir = $curDir . $cd;
   chop $curDir if /.*\\$/;
   $checkDir = $curDir;

   unless( -d $checkDir )
   {
      $res = mkdir $checkDir unless -d $checkDir;
      exit 1 unless $res == 1;
   }
} continue
{
   $i++;
}

exit 0;