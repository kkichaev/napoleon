#
# создает cab файл
# параметры outDir CabFileName Compress=[true,false] [ProgName=ProgName] [SetupDLL=NameOfSetupDll] [DestDir=File:Dir] file1 [fileN]
#
# CabFileName - только имя файла (без пути)
# file1 - fileN вместе с путями
# в выхоодной папке должен быть NapoleonSetup.dll
#

use File::Copy;

$outDir = shift @ARGV;
# заменим / на \
$outDir =~ s-/-\\-g;

$cab = shift @ARGV;
$inf = $cab;
$inf =~ s/cab/inf/;

$Compress = shift @ARGV;
if( $Compress =~ /^Compress=(.*)/ )
{
   if( $1 =~ /true/ )
   {
     $Compress = "/compress";
   }
   else
   {
     $Compress = "";
   }
} else
{
   unshift (@ARGV, $Compress);
   $Compress = "/compress";
}

$AppName = shift @ARGV;
if( $AppName =~ /^ProgName=(.*)/ )
{
   $AppName = $1;
} else
{
   unshift (@ARGV, $AppName);
   $AppName = substr ($cab, 0, length($cab) - 4);
}

$tempDir = $outDir . "\\temp";
mkdir $tempDir;
unlink $tempDir . "\\*.*";

$SetupFile = "";

$fn = <<EOF;
[SourceDisksFiles]                  ; list of files to be included in .cab
EOF

$DestDir = <<EOF;
[DestinationDirs]                       ; default destination directories for each operation section
CopyToProgramFiles    = 0,%InstallDir%
Shortcuts             = 0,%CE17%       ; \\Windows\\Start Menu
EOF

$checkSetup = 0;
# Ищем SetupDLL и определяем переменный дял работы
$CheckSetup = shift @ARGV;
if( $CheckSetup =~ /^SetupDLL=(.*)/ )
{
   $setup = $1;
   die "no $setup" if not -f $outDir . "\\" . $setup;
   copy($outDir . "\\" . $setup, $tempDir . "\\" . $setup);

   $SetupFile = "CESetupDLL  = $setup";
   $fn .= "$setup = 1\n";

   $checkSetup = 1;

} else
{
   unshift (@ARGV, $CheckSetup);
}

# ищеи информацию по папкам
$CopyFiles = "CopyFiles   = CopyToProgramFiles";
%sections = ();
$index = 0;
while( ($_ = shift @ARGV) =~ /DestDir=(.*):(.*)/ )
{
   $folder = "ProgFolder" . $index++;

   $DestDir .= "$folder  = 0,\%$2\%\n";
   $CopyFiles .= ",$folder";

   @sec = "\n[$folder]\n";

   @sections{$1} = @sec;
}
unshift (@ARGV, $_);

# заголовок
$head = <<EOF;
[Version]
Signature   = "\$Windows NT\$"        ; required as-is
Provider    = "GRSoft"
CESignature = "\$Windows CE\$"        ; required as-is
 
[CEStrings]
AppName     = "$AppName"
InstallDir  = %CE1%\\%AppName%       ; Program Files\\Napoleon

[CEDevice]
VersionMin 		= 5.0
VersionMax		= 7.0

[SourceDisksNames]                  ; directory that holds the application's files
1 = , "Common files",,.\\
 
[DefaultInstall]                    ; operations to be completed during install
$CopyFiles
AddReg      = RegData
CEShortcuts = Shortcuts   
$SetupFile
 
$DestDir
 
[RegData]                           ; registry key list
HKCU,Software\\%AppName%,MajorVersion,0x00010001,1
HKCU,Software\\%AppName%,MinorVersion,0x00010001,1
HKCU,Software\\%AppName%,Folder,0x00000000,%InstallDir%

EOF

$sh = <<EOF;
[Shortcuts]                         ; Shortcut created in destination dir, %CE17%
EOF

$cf = <<EOF;
[CopyToProgramFiles]                ; copy operation file list
EOF


$loaded = 0;
for my $var (@ARGV)
{
   # заменим / на \
   $var =~ s-/-\\-g;
   if( -f $var )
   {
      @v = split(/\\/, $var);
      $count = #@v;
      $fileName = @v[$count-1];
      if( $fileName =~ /\.exe$/ && $loaded == 0 )
      {
         $f1 = substr ($fileName, 0, length($fileName) - 4);
         $sh .= "\"" . $f1  . "\",0,\"" . $fileName . "\",\"%CE11%\" ;\\Windows\\Start Menu\\Programs\n\n";
         $loaded = 1;
      }

      if( $checkSetup == 0 or $fileName !~ $setup )
      {
         $dest = $tempDir . "\\" . $fileName;
         copy($var, $dest);

         $fn .= $fileName . " = 1\n";

         if( exists $sections{$fileName} )
         {
            $sections{$fileName} .= "\"" . $fileName . "\", " . $fileName . "\n";
         } else
         {
            $cf .= "\"" . $fileName . "\", " . $fileName . "\n";
         }
      }
   }
}

while( ($key, $value) = each %sections )
{
   $cf .= $value;
}

$outName = $tempDir . "\\" . $inf;
open(RES, "> $outName");
print RES $head . $sh . $fn . "\n" . $cf;
close(RES);

$prog = <<EOF;
\@cd "$tempDir"
\@"C:\\Program Files (x86)\\Microsoft Visual Studio 9.0\\SmartDevices\\SDK\\SDKTools\\cabwiz.exe" $inf $Compress
EOF

$cmdFile = $tempDir . "\\temp.bat";
open(CMD, "> $cmdFile");
print CMD $prog;
close(CMD);

system $cmdFile;

$cabFile = $tempDir . "\\" . $cab;
copy($cabFile, $outDir . "\\" . $cab) if( -f $cabFile );

exit 0;