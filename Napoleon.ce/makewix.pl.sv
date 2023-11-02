#
# Главный файл WIX
#
# Параметры outFile File [File]
#

$outFile = shift @ARGV;


$head = <<EOF;
<?xml version='1.0' encoding="windows-1251"?>
<Wix xmlns='http://schemas.microsoft.com/wix/2006/wi'>
   <Product Id='74141A7C-84F7-4a21-99CD-177CC646ACCD' Name='АСМТ Наполеон' Language='1049' UpgradeCode='362FAB69-3656-4df2-ABBF-C09E5B12036B'
            Version='3.1.0.2' Manufacturer='Гильдия разработчиков'>
      <Package Id='*'
                Description='Автоматизированная система мобильной торговли НАПОЛЕОН'
                Comments='Инсталляция комплекса АСМТ Наполеон'
                Manufacturer='Гильдия разработчиков' InstallerVersion='200' Compressed='yes' />
      <Media Id='1' Cabinet='product.cab' EmbedCab='yes' />
      <Property Id="DISABLEADVTSHORTCUTS" Value="1" />
      <WixVariable Id="WixUILicenseRtf" Value="License.rtf" />
      <WixVariable Id="WixUIBannerBmp" Value="Bitmaps\\bannrbmp.bmp" />
      <WixVariable Id="WixUIDialogBmp" Value="Bitmaps\\dlgbmp.bmp" />
      <WixVariable Id="WixUIExclamationIco" Value="Bitmaps\\exclamic.ico" />
      <WixVariable Id="WixUIInfoIco" Value="Bitmaps\\info.ico" />
      <WixVariable Id="WixUINewIco" Value="Bitmaps\\New.ico" />
      <WixVariable Id="WixUIUpIco" Value="Bitmaps\\Up.ico" />
EOF

$tail = <<EOF;
      <UIRef Id="WixUI_Mondo" />
   </Product>
</Wix>
EOF

open(OUT, "> $outFile");
print OUT $head;

for my $file (@ARGV)
{
   if( $file =~ /(Folders|Common|Add)/ ) {}
   else {
      print OUT "      <FeatureRef Id='$file'/>\n"; }
}

print OUT $tail;

close(OUT);