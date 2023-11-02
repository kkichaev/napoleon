import sys
import xml.etree.ElementTree as ET

def loadStrings(tree:ET) -> tuple[dict[str, ET.Element], dict[str, ET.Element]]:
    strings : dict[str, ET.Element] = {}
    stringArrays : dict[str, ET.Element] = {}

    for el in tree.iter('string'):
        strings[el.get('name')] = el

    for el in tree.iter('string-array'):
        stringArrays[el.get('name')] = el

    return (strings, stringArrays)

def checkAndWrite(s1:dict[str, ET.Element], s2:dict[str, ET.Element], rootEl) -> bool:
    sdiff = s1.keys() - s2.keys()
    if len(sdiff) == 0:
        return False

    for k in sdiff:
        e = s1[k]
        ch = ET.SubElement(rootEl, e.tag, e.attrib)

        if len(e.text) > 0: ch.text = e.text
        
        for elch in e:
            ch1 = ET.SubElement(ch, elch.tag, elch.attrib)
            ch1.text = elch.text

    return True

def writeFile(tree:ET, fileName:str) -> None:
    print('Write file ' + fileName)
    ET.indent(tree, '    ')
    tree.write(fileName, encoding='utf-8', xml_declaration=True)
    


def merge():
    path1 = sys.argv[1] + ("" if sys.argv[1].endswith('\\') else '\\' )
    path2 = sys.argv[2] + ("" if sys.argv[1].endswith('\\') else '\\' )
    f1 = path1 + 'strings.xml'
    f2 = path2 + 'strings.xml'

    tree1 = ET.parse(f1)
    tree2 = ET.parse(f2)

    (s1, sa1) = loadStrings(tree1)
    (s2, sa2) = loadStrings(tree2)

    s = s2 | s1
    sa = sa2 | sa1

    ch1 = checkAndWrite(s2, s1, tree1.getroot()) or \
          checkAndWrite(sa2, sa1, tree1.getroot())
    if ch1: writeFile(tree1, path1 + 'strings.out.xml')
    else: print('No changes in ' + path1)

    ch2 =  checkAndWrite(s1, s2, tree2.getroot()) or \
            checkAndWrite(sa1, sa2, tree2.getroot())
    if ch2: writeFile(tree2, path2 + 'strings.out.xml')
    else: print('No changes in ' + path2)


if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("%s valueFolder valueOtherFolder" % sys.argv[0])
        exit(1)
    merge()