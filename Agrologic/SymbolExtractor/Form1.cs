using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.Drawing;
using System.IO;
using System.Text;
using System.Windows.Forms;
using System.Xml;
using System.Xml.Serialization;

namespace SymbolsExtractor
{
   public partial class Form1 : Form
   {
      static readonly string VERSION = "1.02";

      string inputFile;
      string fontFolder;
      string outFolder;
      string progName = "lv_font_conv";
      //string prefix = "out";
      //string ext = "";
      //int formatIndex = 2;
      //int bpp = 4;

      double fontCoef = 1.0;
      AddFontData addData;

      public static List<int> langSymbols = new List<int>();

      Dictionary<FontData, List<int>> fonts = new Dictionary<FontData, List<int>>();
      List<int> graphSymbols = new List<int>();

      public Form1()
      {
         InitializeComponent();

         DirectoryInfo dirWindowsFolder = Directory.GetParent(Environment.GetFolderPath(Environment.SpecialFolder.System));
         fontFolder = Path.Combine(dirWindowsFolder.FullName, "Fonts");

         outFolder = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments);
         outFolder += "\\SymbolsExtractor";

         Config c = ReadConfig();
         progName = c.ProgName;
         fontFolder = c.FontFolder;
         outFolder = c.OutFolder;
         cbMakeBat.Checked = c.MakeBat;
         cbCompress.Checked = c.Compress;
         tbFlags.Text = c.AddOpt;


         cbFormat.SelectedIndex = c.FormatIndex;
         lbOutFolder.Text = outFolder;
         tbPrefix.Text = c.Prefix;
         tbSuffix.Text = c.Ext; ;
         lbProgName.Text = progName;
         lbFontFolder.Text = fontFolder;
         nmBPP.Value = c.BPP;
         tbCoef.Text = c.FontCoef.ToString();

         Text = "Symbol extractor v" + VERSION;
      }

      Config ReadConfig()
      {
         string fn = Application.UserAppDataPath + "\\AppCfg.xml";
         if (File.Exists(fn))
         {
            XmlSerializer formatter = new XmlSerializer(typeof(Config));
            using (FileStream fs = new FileStream(fn, FileMode.Open))
            {
               Config c;
               try
               {
                  c = (Config)formatter.Deserialize(fs);
               }
               catch (Exception e)
               {
                  c = new Config();
               }
               return c;
            }
         }
         return new Config();
      }

      void WriteConfig()
      {
         Config c = new Config();
         c.ProgName = progName;
         c.BPP = (int)nmBPP.Value;
         c.Ext = tbSuffix.Text;
         c.FontFolder = fontFolder;
         c.FormatIndex = cbFormat.SelectedIndex;
         c.OutFolder = outFolder;
         c.Prefix = tbPrefix.Text;
         c.MakeBat = cbMakeBat.Checked;
         c.Compress = cbCompress.Checked;
         c.AddOpt = tbFlags.Text;

         if (!double.TryParse(tbCoef.Text.Replace(',', '.'), System.Globalization.NumberStyles.Float, System.Globalization.CultureInfo.InvariantCulture, out fontCoef))
            fontCoef = 1;
         c.FontCoef = fontCoef;

         Directory.CreateDirectory(Application.UserAppDataPath);

         string fn = Application.UserAppDataPath + "\\AppCfg.xml";
         XmlSerializer formatter = new XmlSerializer(typeof(Config));
         using (FileStream fs = new FileStream(fn, FileMode.Create))
         {
            formatter.Serialize(fs, c);
         }
      }

      protected override void OnClosing(CancelEventArgs e)
      {
         WriteConfig();
         base.OnClosing(e);
      }

      private void button1_Click(object sender, EventArgs e)
      {
         OpenFileDialog ofd = new OpenFileDialog();
         ofd.Filter = "Alcxml (*.alcxml)|*.alcxml|All files (*.*)|*.*";
         if (ofd.ShowDialog() == System.Windows.Forms.DialogResult.OK)
         {
            lbInputFile.Text = ofd.FileName;
            inputFile = ofd.FileName;

            List<FontInfo> src = new List<FontInfo>();
            if (ProcessFile())
            {
               foreach (KeyValuePair<FontData, List<int>> kv in fonts)
               {
                  kv.Key.MatchFont(fontFolder);
                  FontInfo fi = new FontInfo(kv.Key, kv.Value.Count);
                  src.Add(fi);
               }
            }
            src.Sort();
            dgvFonts.DataSource = src;
         }
      }

      bool ProcessFile()
      {
         bool ret = true;
         fonts.Clear();
         try
         {
            bool readProp = false, readControl = false; ;
            XmlTextReader reader = new XmlTextReader(inputFile);
            ControlTextData data = new ControlTextData();
            while (reader.Read())
            {
               switch (reader.NodeType)
               {
                  case XmlNodeType.Element: // Узел является элементом.
                     if (reader.Name == "ScreenControl")
                     {
                        readControl = true;
                        data = new ControlTextData();
                     }
                     else if (readControl)
                     {
                        readProp = (reader.Name == "Prop");
                     }
                     break;
                  case XmlNodeType.Text: // Вывести текст в каждом элементе.
                     if (readProp)
                        ReadingProp(data, reader.Value);
                     break;
                  case XmlNodeType.EndElement:
                     if (reader.Name == "ScreenControl")
                     {
                        if (data.HaveData)
                        {
                           AddToFonts(data);
                        }
                        readControl = false;
                     }
                     break;
               }
            }
         }
         catch (Exception e)
         {
            ret = false;
            MessageBox.Show(e.Message, "Error while process file", MessageBoxButtons.OK, MessageBoxIcon.Stop);
         }
         return ret;
      }

      static public void AddSymbols(List<int> chars, string text)
      {
         byte[] b = Encoding.UTF32.GetBytes(text);
         for (int i = 0; i < b.Length; i += 4)
         {
            int value = BitConverter.ToInt32(b, i);
            if (!chars.Contains(value))
               chars.Add(value);
         }
      }

      private void AddToFonts(ControlTextData data)
      {
         List<int> chars;
         if (!fonts.TryGetValue(data.font, out chars))
         {
            chars = new List<int>();
            fonts[data.font] = chars;
         }

         AddSymbols(chars, data.text);
         AddSymbols(chars, data.translation);
      }

      List<string> graphPropNames = new List<string>(new string[]
      {
            "Plot1_DataIdText",
            "Plot1_Description",
            "Plot1_IntervalDescription",
            "Plot1_Units",
            "Plot2_DataIdText",
            "Plot2_Description",
            "Plot2_IntervalDescription",
            "Plot2_Units",
            "Plot3_DataIdText",
            "Plot3_Description",
            "Plot3_IntervalDescription",
            "Plot3_Units",
            "Plot4_DataIdText",
            "Plot4_Description",
            "Plot4_IntervalDescription",
            "Plot4_Units",
      });
      bool isGraphProp(string name)
      {
         return graphPropNames.Contains(name);
      }
      private void ReadingProp(ControlTextData ctrl, string p)
      {
         string[] data = p.Split(new char[] { ':' }, 2);
         if (data.Length == 2)
         {
            if (data[0] == "Text")
               ctrl.text = data[1];
            else if (data[0] == "Translation")
               ctrl.translation += data[1];
            else if (data[0] == "Description")
               ctrl.translation += data[1];
            else if (data[0] == "States")
               ctrl.translation += data[1];
            else if (data[0] == "Font")
               ctrl.font = FontData.FromProp(data[1]);
            if (isGraphProp(data[0]))
            {
               AddSymbols(graphSymbols, data[1]);
            }
         }
      }

      private void button2_Click(object sender, EventArgs e)
      {
         FolderBrowserDialog fbd = new FolderBrowserDialog();
         fbd.SelectedPath = fontFolder;
         if (fbd.ShowDialog() == System.Windows.Forms.DialogResult.OK)
         {
            fontFolder = fbd.SelectedPath;
            lbFontFolder.Text = fontFolder;

            if (fonts.Count > 0)
            {
               List<FontInfo> src = new List<FontInfo>();
               foreach (KeyValuePair<FontData, List<int>> kv in fonts)
               {
                  kv.Key.MatchFont(fontFolder);
                  FontInfo fi = new FontInfo(kv.Key, kv.Value.Count);
                  src.Add(fi);
               }

               src.Sort();
               dgvFonts.DataSource = src;
            }
         }
      }

      private void dgvFonts_CellDoubleClick(object sender, DataGridViewCellEventArgs e)
      {
         if (dgvFonts.Columns[e.ColumnIndex].DataPropertyName == "FileName")
         {
            FontInfo fi = dgvFonts.Rows[e.RowIndex].DataBoundItem as FontInfo;
            SelectFont sf = new SelectFont();
            sf.SetFolder(fontFolder);
            if (sf.ShowDialog() == System.Windows.Forms.DialogResult.OK)
            {
               fi.FileName = sf.SelectedFile;
               dgvFonts.InvalidateCell(e.ColumnIndex, e.RowIndex);
            }
         }
      }

      private void button3_Click(object sender, EventArgs e)
      {
         FolderBrowserDialog fbd = new FolderBrowserDialog();
         fbd.SelectedPath = outFolder;
         if (fbd.ShowDialog() == System.Windows.Forms.DialogResult.OK)
         {
            outFolder = fbd.SelectedPath;
            lbOutFolder.Text = outFolder;
         }
      }

      Dictionary<FontData, List<int>> PrepareFontToWrite()
      {
         if (addData == null)
            return fonts;

         Dictionary<FontData, List<int>> ret = new Dictionary<FontData, List<int>>();

         foreach (KeyValuePair<FontData, List<int>> kv in fonts)
         {
            List<int> symbols = new List<int>(kv.Value);
            AddSymbols(symbols, addData.AddSymbols);
            ret[kv.Key] = symbols;
         }

         foreach (FontData fd in addData.Fonts)
         {
            List<int> symbols = new List<int>();
            if (ret.ContainsKey(fd))
               symbols = ret[fd];
            AddSymbols(symbols, addData.FontSymbols);
            ret[fd] = symbols;
         }

         return ret;
      }

      string makeRunStr(string flags, FontData fd, string outName)
      {
         string runStr = flags;
         runStr += " --font \"" + fd.fileName + "\"";
         int fs = (int)(fd.Size * fontCoef + 0.0005);
         runStr += " --size " + fs.ToString();
         string suffix = "";
         if (tbSuffix.Text.Length > 0)
            suffix = "." + tbSuffix.Text;
         runStr += " -o \"" + outFolder + "\\" + outName + "\"";

         return runStr;
      }

      List<string> writeGraphData(string flags)
      {
         List<string> ret = new List<string>();
         List<int> values = new List<int>(graphSymbols);

         if (addData.AddSymbols.Length > 0)
            AddSymbols(values, addData.AddSymbols);

         foreach(int sym in langSymbols)
            if (!values.Contains(sym))
               values.Add(sym);

         foreach (FontData fd in addData.AlcGraphData)
         {
            if (fd.FontFile.Length == 0 || fd.Size == 0) continue;

            string runStr = makeRunStr(flags, fd, fd.FontName.Replace(" ", "_") + ".cpp");

            string range = "";
            values.Sort();
            foreach (int val in values)
               range += "0x" + val.ToString("X") + ",";

            runStr += " -r " + range.Substring(0, range.Length - 1);

            ret.Add(runStr);
         }

         return ret;
      }

      public void WriteFiles(Dictionary<FontData, List<int>> wrFonts, bool writeGraph)
      {
         int outCount = 1;

         if (!double.TryParse(tbCoef.Text.Replace(',', '.'), System.Globalization.NumberStyles.Float, System.Globalization.CultureInfo.InvariantCulture, out fontCoef))
            fontCoef = 1;

         Directory.CreateDirectory(outFolder);

         List<string> progs = new List<string>();

         string flags = tbFlags.Text;
         flags += " --format " + cbFormat.SelectedItem as string;
         flags += " --bpp " + ((int)nmBPP.Value).ToString();
         if (!cbCompress.Checked)
            flags += " --no-compress";


         List<string> outStr = new List<string>();

         foreach (KeyValuePair<FontData, List<int>> kv in wrFonts)
         {
            if (kv.Key.fileName.Length == 0)
               continue;

            string suffix = "";
            if (tbSuffix.Text.Length > 0)
               suffix = "." + tbSuffix.Text;
            string fileBase = kv.Key.FileBase;
            string runStr = makeRunStr(flags, kv.Key, tbPrefix.Text + fileBase + suffix);
            //string runStr = flags;
            //runStr += " --font \"" + kv.Key.fileName + "\"";
            //int fs = (int)(kv.Key.size * fontCoef + 0.0005);
            //runStr += " --size " + fs.ToString();
            //runStr += " -o \"" + outFolder + "\\" + tbPrefix.Text + fileBase + suffix + "\"";

            string range = "";
            kv.Value.Sort();
            foreach (int val in kv.Value)
               range += "0x" + val.ToString("X") + ",";

            runStr += " -r " + range.Substring(0, range.Length - 1);

            outStr.Add(runStr);
            outCount++;
         }

         if (writeGraph)
         {
            List<string> graph = writeGraphData(flags);
            outStr.AddRange(graph);
         }

         foreach (string str in outStr)
         {
            if (cbMakeBat.Checked)
            {
               progs.Add("call " + progName + " " + str);
            }
            else
            {
               try
               {
                  Process.Start(progName, str);
               }
               catch (Exception er)
               {
                  MessageBox.Show(er.Message);
                  break;
               }
            }
         }

         if (cbMakeBat.Checked)
         {
            string fn = outFolder + "\\" + Path.GetFileNameWithoutExtension(inputFile) + ".bat";
            File.WriteAllLines(fn, progs.ToArray());
         }
      }

      private void button4_Click(object sender, EventArgs e)
      {
         Dictionary<FontData, List<int>> wrFonts = PrepareFontToWrite();
         WriteFiles(wrFonts, true);

         if (cbMakeBat.Checked)
         {
            MessageBox.Show("Done");
         }
      }

      private void button5_Click(object sender, EventArgs e)
      {
         OpenFileDialog ofd = new OpenFileDialog();
         ofd.FileName = progName;
         if (ofd.ShowDialog() == System.Windows.Forms.DialogResult.OK)
         {
            progName = ofd.FileName;
            lbProgName.Text = progName;
         }
      }

      private void button6_Click(object sender, EventArgs e)
      {
         if (fontFolder == null || fontFolder.Length == 0)
         {
            MessageBox.Show("Please, select Font folder before.");
            return;
         }
         FmAddFonts f = new FmAddFonts();
         f.FontFolder = fontFolder;
         if (addData != null)
            f.Data = addData;
         if (f.ShowDialog() == System.Windows.Forms.DialogResult.OK)
            addData = f.Data;
      }

      private void button7_Click(object sender, EventArgs e)
      {
         if (inputFile == null || inputFile.Length == 0)
         {
            MessageBox.Show("Please, select source file before");
            return;
         }

         FmIntFontSelect fm = new FmIntFontSelect();
         fm.setData(inputFile, outFolder, fontFolder, this);
         fm.Show();
      }
   }

   public class FontData
   {
      public string name = "";
      public bool bold = false;
      public bool italic = false;
      public int size = 0;

      public string fileName = "";

      public bool HaveData { get { return size > 0 && name.Length > 0; } }

      public override int GetHashCode()
      {
         return name.GetHashCode() ^ size ^ (bold ? 0x7FFF0000 : 0) ^ (italic ? 0xFFFF : 0);
      }

      public override bool Equals(object obj)
      {
         FontData other = obj as FontData;
         if (other == null)
            return false;
         return (name == other.name) && (bold == other.bold) && (italic == other.italic) && (size == other.size);
      }

      [XmlIgnore]
      public string FontName { get { return name; } set { name = value; } }
      [XmlIgnore]
      public string FontFile { get { return fileName; } set { fileName = value; } }
      [XmlIgnore]
      public int Size { get { return size; } set { size = value; } }
      [XmlIgnore]
      public bool Bold { get { return bold; } set { bold = value; } }
      [XmlIgnore]
      public bool Italic { get { return italic; } set { italic = value; } }

      public string Name
      {
         get
         {
            return name + ", " + size.ToString() + (bold ? " Bold" : "") + (italic ? " Italic" : "");
         }
      }

      public static FontData FromProp(string value)
      {
         FontData ret = new FontData();

         string[] data = value.Split(',');
         ret.name = data[0];

         string sz = data[1].TrimStart();
         int pos = sz.IndexOf('.');
         if (pos < 0)
            pos = sz.IndexOf("pt");
         int.TryParse(sz.Substring(0, pos), out ret.size);
         ret.bold = value.Contains("Bold");
         ret.italic = value.Contains("Italic");

         return ret;
      }

      public void MatchFont(string folder)
      {
         fileName = "";
         string fname = folder + "\\" + name;
         if (bold && !italic)
            fname += "bd";
         else if (bold && italic)
            fname += "bi";
         else if (italic)
            fname += "i";
         fname += ".ttf";

         if (File.Exists(fname))
            fileName = fname;
      }

      public int BI { get { return (bold ? 1 : 0) + (italic ? 2 : 0); } }

      public string FileBase
      {
         get
         {
            string ret = name.Replace(" ", "_") + "_" + size.ToString() + (bold ? "_Bold" : "") + (italic ? "_Italic" : "");
            if (AddFileBase != null)
               ret += AddFileBase;
            return ret;
         }
      }

      public string AddFileBase { get; set; }
   }

   class ControlTextData
   {
      public string text = "";
      public string translation = "";

      public FontData font = new FontData();

      public bool HaveData { get { return font.HaveData && (text.Length > 0 || translation.Length > 0); } }
   }

   class FontInfo : IComparable<FontInfo>
   {
      FontData fd;
      public FontInfo(FontData font, int symbols)
      {
         SymbolCount = symbols;
         fd = font;
      }

      public string Font { get { return fd.Name; } }
      public int SymbolCount { get; set; }
      public string FileName { get { return fd.fileName; } set { fd.fileName = value; } }

      public int CompareTo(FontInfo other)
      {
         int cmp = fd.name.CompareTo(other.fd.name);
         if (cmp != 0)
            return cmp;

         cmp = fd.size - other.fd.size;
         if (cmp != 0)
            return cmp;

         return fd.BI - other.fd.BI;
      }
   }

   public class Config
   {
      public Config()
      {
         ProgName = "lv_font_conv";
         DirectoryInfo dirWindowsFolder = Directory.GetParent(Environment.GetFolderPath(Environment.SpecialFolder.System));
         FontFolder = Path.Combine(dirWindowsFolder.FullName, "Fonts");

         OutFolder = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments);
         OutFolder += "\\SymbolsExtractor";

         Prefix = "out";
         Ext = "";

         FormatIndex = 2;
         BPP = 4;
         MakeBat = false;
         Compress = false;
         AddOpt = "";
         FontCoef = 1.0;
      }

      public string ProgName { get; set; }
      public string FontFolder { get; set; }
      public string OutFolder { get; set; }
      public string Prefix { get; set; }
      public string Ext { get; set; }
      public int FormatIndex { get; set; }
      public int BPP { get; set; }
      public bool MakeBat { get; set; }
      public bool Compress { get; set; }
      public string AddOpt { get; set; }

      public double FontCoef { get; set; }
   }
}
