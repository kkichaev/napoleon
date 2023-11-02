# -*- coding: cp1251 -*-
#
# Главный файл WIX
#
# Параметры outFile File [File]
#
import sys
import os

outFile = sys.argv[1]

platform = "Platform='x64'" if 'SERVER_X64' in os.environ else ''

head = """<?xml version='1.0' encoding="windows-1251"?>
<Wix xmlns='http://schemas.microsoft.com/wix/2006/wi'>
   <Product Id='74141A7C-84F7-4a21-99CD-177CC646ACCD' Name='АСМТ Наполеон' Language='1049' UpgradeCode='362FAB69-3656-4df2-ABBF-C09E5B12036B'
            Version='3.1.0.2' Manufacturer='Гильдия разработчиков' Codepage='1251'>
      <Package Id='*'
                Description='Автоматизированная система мобильной торговли НАПОЛЕОН'
                Comments='Инсталляция комплекса АСМТ Наполеон'
                Manufacturer='Гильдия разработчиков' InstallerVersion='200' Compressed='yes' {platform} SummaryCodepage='1251' />
      <Media Id='1' Cabinet='product.cab' EmbedCab='yes' />
      <Property Id="DISABLEADVTSHORTCUTS" Value="1" />
      <WixVariable Id="WixUILicenseRtf" Value="License.rtf" />
      <WixVariable Id="WixUIBannerBmp" Value="Bitmaps\\bannrbmp.bmp" />
      <WixVariable Id="WixUIDialogBmp" Value="Bitmaps\\dlgbmp.bmp" />
      <WixVariable Id="WixUIExclamationIco" Value="Bitmaps\\exclamic.ico" />
      <WixVariable Id="WixUIInfoIco" Value="Bitmaps\\info.ico" />
      <WixVariable Id="WixUINewIco" Value="Bitmaps\\New.ico" />
      <WixVariable Id="WixUIUpIco" Value="Bitmaps\\Up.ico" />

      <Upgrade Id="362FAB69-3656-4df2-ABBF-C09E5B12036B">  
          <UpgradeVersion OnlyDetect='yes' Property='SELFFOUND'
            Minimum='3.1.0.0' IncludeMinimum='yes'
            Maximum='4.1.0.1' IncludeMaximum='yes' />
          <UpgradeVersion OnlyDetect='yes' Property='NEWERFOUND'
            Minimum='4.1.0.1' IncludeMinimum='no' />
      </Upgrade> 

      <CustomAction Id='AlreadyUpdated' Error='АСМТ Наполеон уже установлен' />
      <CustomAction Id='NoDowngrade' Error='Последняя версия [ProductName] уже установлена' />

      <InstallExecuteSequence>
            <Custom Action='AlreadyUpdated' After='FindRelatedProducts'>SELFFOUND</Custom>
            <Custom Action='NoDowngrade' After='FindRelatedProducts'>NEWERFOUND</Custom>
      </InstallExecuteSequence>
""".format(platform=platform)

headAds = """<?xml version='1.0' encoding="windows-1251"?>
<Wix xmlns='http://schemas.microsoft.com/wix/2006/wi'>
   <Product Id='25A053EB-DE99-4a76-945B-FE26F35E6596' Name='Наполеон АДС' Language='1049' UpgradeCode='6E9AA21B-E471-4a97-8F75-A49A10C9AFF8'
            Version='3.1.0.1' Manufacturer='Гильдия разработчиков' Codepage='1251'>
      <Package Id='*'
                Description='НАПОЛЕОН АДС'
                Comments='Инсталляция комплекса Наполеон АДС'
                Manufacturer='Гильдия разработчиков' InstallerVersion='200' Compressed='yes' SummaryCodepage='1251' />
      <Media Id='1' Cabinet='product.cab' EmbedCab='yes' />
      <Property Id="DISABLEADVTSHORTCUTS" Value="1" />
      <WixVariable Id="WixUILicenseRtf" Value="License.rtf" />
      <WixVariable Id="WixUIBannerBmp" Value="Bitmaps\\bannrbmp.bmp" />
      <WixVariable Id="WixUIDialogBmp" Value="Bitmaps\\dlgbmp.bmp" />
      <WixVariable Id="WixUIExclamationIco" Value="Bitmaps\\exclamic.ico" />
      <WixVariable Id="WixUIInfoIco" Value="Bitmaps\\info.ico" />
      <WixVariable Id="WixUINewIco" Value="Bitmaps\\New.ico" />
      <WixVariable Id="WixUIUpIco" Value="Bitmaps\\Up.ico" />
"""

tail = """
      <UIRef Id="WixUI_Mondo" />
   </Product>
</Wix>
"""

def containsAnyEl(array, array2):
   for el in array:
     if el in array2: return True
   return False

def strInFeature(val, features):
   for ftr in features:
      if val.startswith(ftr): return True
   return False


vars = sys.argv[2:]

file = open(outFile, "w")
file.write(headAds if "AdsManager" in vars else head)

for val in vars:
  if val == "Server":    
    if containsAnyEl(vars, ["PythonReporter", "PythonReporter_2_7_14", "PythonReporter_3_8", "PythonReporter_3_9"]): 
      file.write("""
      <FeatureRef Id='Server'>
            <FeatureRef Id='PythonReporter' />
      </FeatureRef>
      """)
    else: file.write("      <FeatureRef Id='Server'/>\n")
  elif val == 'Server_x64':
      file.write("""
      <FeatureRef Id='Server'>
            <FeatureRef Id='PythonReporter_x64' />
      </FeatureRef>
      """)
  elif val == 'Manager' and containsAnyEl(vars, ['ManagerXls']):
       file.write( """
      <FeatureRef Id='Manager' >
         <ComponentRef Id='ExcelLibrary' />
      </FeatureRef>
       """)
  else:
    if not strInFeature(val, ['Folders','Common','Add','PythonReporter','pylib','expfolder','ManagerXls', 'webview']): 
       file.write( "      <FeatureRef Id='{val}'/>\n".format(val=val))

file.write(tail)
file.close()
