using Greatis.FormDesigner;
using System.Xml;
using System.Collections;
using System.Text;
using System.IO;
namespace NFormEditor
{
   class UnicodeStringWriter : StringWriter
   {
      public override Encoding Encoding
      {
         get
         {
            return Encoding.Unicode;
         }
      }
   }

   class PageReader : XMLFormReader
   {
      public PageReader(string fileName) : base(fileName)
      {
      }

      public PageReader(XmlReader reader)
         : base(reader)
      {
      }

      public override bool Read()
      {
         if (!base.Read())
            return false;

         if (State == ReaderState.StartElement || State == ReaderState.EndElement)
         {
            if (item.StartsWith("Cells"))
            {
               item = item.Replace("Cells", "Item");
               attrmap.Clear();
               attrmap["collection"] = "true";
               attrmap["itemtype"] = "NFormEditor.Cell, NFormEditor, Version=1.0.0.0, Culture=neutral, PublicKeyToken=null";
            }
            else if (item == "Rows")
            {
               attrmap.Clear();
               attrmap["collection"] = "true";
               attrmap["itemtype"] = "NFormEditor.TableRow, NFormEditor, Version=1.0.0.0, Culture=neutral, PublicKeyToken=null";
            }
            else if (item == "TableWidth")
            {
               attrmap.Clear();
               attrmap["collection"] = "true";
               attrmap["itemtype"] = "System.Int32, mscorlib, Version=2.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089  ";
            }
            else if (item == "TableHeight")
            {
               attrmap.Clear();
               attrmap["collection"] = "true";
               attrmap["itemtype"] = "System.Int32, mscorlib, Version=2.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089  ";
            }
            else if (attrmap.Contains("name"))
            {
               string val = (string)attrmap["name"];
               string assStr;
               if (val.IndexOf("Line") >= 0)
                  assStr = "NFormEditor.Line, NFormEditor, Version=1.0.0.0, Culture=neutral, PublicKeyToken=null";
               else if (val.IndexOf("Table") >= 0)
                  assStr = "NFormEditor.Table, NFormEditor, Version=1.0.0.0, Culture=neutral, PublicKeyToken=null";
               else if (val.IndexOf("Row") >= 0)
                  assStr = "NFormEditor.Row, NFormEditor, Version=1.0.0.0, Culture=neutral, PublicKeyToken=null";
               else if (val.IndexOf("Picture") >= 0)
                  assStr = "NFormEditor.Picture, NFormEditor, Version=1.0.0.0, Culture=neutral, PublicKeyToken=null";
               else if (val.Length > 0)
                  assStr = "NFormEditor.Label, NFormEditor, Version=1.0.0.0, Culture=neutral, PublicKeyToken=null";
               else
                  assStr = "System.Windows.Forms.Panel, System.Windows.Forms, Version=2.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089";

               attrmap["assembly"] = assStr;
            }

         }
         return true;
      }
   }

   class PageWriter : XMLFormWriter
   {
      bool writeBindings = false;
      bool writeForm = false;

      string header = null;
      XmlWriter writer;

      public PageWriter(XmlTextWriter writer, string header) : base(writer)
      {
         this.writer = writer;
         this.header = header;
      }

      //public PageWriter(XmlWriter writer)
      //   : base(writer)
      //{
      //   this.writer = writer;
      //}

      override public void WriteStartElement(string name, Hashtable attributes)
      {
         if (name == "object")
         {
            if (attributes != null && attributes["TreasuryVersion"] != null)
               writeForm = true;
            else
               writeForm = false;
         }

         if (writeBindings)
            return;

         if( name == "Album" && writeForm && header != null)
         {
            writer.WriteRaw(header);
         }

         if (name == "DataBindings")
         {
            writeBindings = true;
            return;
         }

         if (attributes != null)
            attributes.Remove("assembly");

         if (name == "Rows")
            attributes.Clear();

         if (name.StartsWith("Item") && attributes != null && attributes.Count > 0)
         {
            name = name.Replace("Item", "Cells");
            attributes.Clear();
         }

         if (name == "TableWidth" || name == "TableHeight")
            attributes.Clear();
         base.WriteStartElement(name, attributes);
      }

      override public void WriteEndElement(string name)
      {
         if (name == "DataBindings")
         {
            writeBindings = false;
            return;
         }

         if (writeBindings)
         {
            return;
         }

        base.WriteEndElement(name);
      }

      override public void WriteValue(string name, string value, Hashtable attributes)
      {
         if (writeBindings)
            return;

         if (writeForm)
         {
            if (!PrintForm.IsWritable(name))
               return;
         }

         if (name == "Name" || name == "Font" )
            return;

         base.WriteValue(name, value, attributes);
      }
   }
}