#
# Главный файл WIX
#
# Параметры outFile File [File]
#

$outFile = shift @ARGV;
$project = shift @ARGV;
$version = shift @ARGV;
$program = shift @ARGV;


$head = <<EOF;
using System.Reflection;
using System.Runtime.CompilerServices;
using System.Runtime.InteropServices;

// Управление общими сведениями о сборке осуществляется с помощью 
// набора атрибутов. Измените значения этих атрибутов, чтобы изменить сведения,
// связанные со сборкой.
[assembly: AssemblyTitle("$program")]
[assembly: AssemblyDescription("АРМ руководителя")]
[assembly: AssemblyConfiguration("")]
[assembly: AssemblyCompany("")]
[assembly: AssemblyCopyright("Copyright ©  2010- 2020, Гильдия Разработчкиов, http://groft.ru")]
[assembly: AssemblyTrademark("")]
[assembly: AssemblyCulture("")]

// Параметр ComVisible со значением FALSE делает типы в сборке невидимыми 
// для COM-компонентов.  Если требуется обратиться к типу в этой сборке через 
// COM, задайте атрибуту ComVisible значение TRUE для этого типа.
[assembly: ComVisible(false)]

// Следующий GUID служит для идентификации библиотеки типов, если этот проект будет видимым для COM
[assembly: Guid("dc6e6465-bc02-4565-b783-5d4edf8bbe94")]

// Сведения о версии сборки состоят из следующих четырех значений:
//
//      Основной номер версии
//      Дополнительный номер версии 
//      Номер построения
//      Редакция
//
// Можно задать все значения или принять номер построения и номер редакции по умолчанию, 
// используя "*", как показано ниже:
// [assembly: AssemblyVersion("1.0.*")]
EOF

open(OUT, "> $outFile");

print OUT $head;
print OUT "[assembly: AssemblyProduct(\"$project\")]\n";
print OUT "[assembly: AssemblyVersion(\"$version\")]\n";
print OUT "[assembly: AssemblyFileVersion(\"$version\")]\n";

close(OUT);