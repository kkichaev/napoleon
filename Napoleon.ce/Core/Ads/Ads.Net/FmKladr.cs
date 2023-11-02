using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Collections;
using System.Xml.Serialization;
using System.IO;
using System.Data.Common;

namespace GRSoft.Ads
{
   public partial class FmKladr : Form
   {
      private static FmKladr instance;
      private KladrTree kladrTree = new KladrTree();

      private AddrSel addrSel = AddrSel.Init();
      private IsOK isOK = null;

      [Serializable]
      public struct AddrSel
      {
         public string kladr;
         public string street;

         public static AddrSel Init()
         {
            AddrSel result = new AddrSel();
            result.kladr = string.Empty;
            result.street = string.Empty;
            return result;
         }

         private static readonly string ADR_SEL_FILE_NAME = "addr.sel";

         public void Save()
         {
            XmlSerializer s = new XmlSerializer(typeof(AddrSel));
            try
            {
               using (TextWriter w = new StreamWriter(ADR_SEL_FILE_NAME))
               {
                  s.Serialize(w, this);
                  w.Close();
               }
            }
            catch (Exception e) { MessageBox.Show(e.Message); }
         }

         public void Load()
         {
            if (File.Exists(ADR_SEL_FILE_NAME))
            {
               XmlSerializer s = new XmlSerializer(typeof(AddrSel));
               using (FileStream fs = new FileStream(ADR_SEL_FILE_NAME, FileMode.Open, FileAccess.Read))
               {
                  try
                  {
                     this = (AddrSel)s.Deserialize(fs);
                  }
                  catch { }
               }
            }
         }
      }

      public FmKladr(IsOK isOK)
         : this()
      {
         this.isOK = isOK;
      }

      public FmKladr()
      {
         InitializeComponent();
         ToolTip tooltip = new ToolTip();
         tooltip.SetToolTip(tbFind, "Начните набирать текст для поиска улицы");
         addrSel.Load();
      }

      public static void ShowInstance()
      {
         if (instance == null)
         {
            instance = new FmKladr();
            instance.Show();
         }
         else
            instance.Activate();
      }

      private DbConnection conn;

      private void FmKladr_Load(object sender, EventArgs e)
      {
         conn = LocalDataBase.Instance().Connection;
         conn.Open();

         DbCommand selectKladr = conn.CreateCommand();
         selectKladr.CommandText = "select code, name, socr from kladr where [code] like '__00000000000'";
         DbDataReader reader = selectKladr.ExecuteReader();
         kladrTree.Load(reader, tvKladr, null);

         if (addrSel.kladr.Length >= 2)
         {
            loadCities(addrSel.kladr);
            tvKladr.SelectedNode = FindNode(addrSel.kladr);

            if(tvKladr.SelectedNode != null)
               loadStreets(addrSel.street);
         }
      }

      private void FmKladr_FormClosed(object sender, FormClosedEventArgs e)
      {
         conn.Close();
         instance = null;
         addrSel.Save();
      }

      private void loadCities(string code)
      {
         if (code.Length > 2)
         {
            string ssCode = code.Substring(0, 2);

            DbCommand selectKladr = conn.CreateCommand();
            selectKladr.CommandText = String.Format("select code, name, socr from kladr where [code] like '{0}%00'", ssCode);
            DbDataReader reader = selectKladr.ExecuteReader();

            String codeSN = selectedNode != null ? ((KladrTreeNode)selectedNode)._object.code : addrSel.kladr;
            KladrTreeNode ktn = (KladrTreeNode)FindNode(code.Substring(0, 2) + "00000000000");

            kladrTree.Load(reader, tvKladr, ktn);

            if (IsCitiesAsRegion(codeSN))
               loadStreets(codeSN);
            else if (addrSel.street.Length > 0)
               loadStreets(addrSel.kladr);
         }
      }

      private static bool IsCitiesAsRegion(String code)
      {
         return SanktPetersburg(code) ||
                        Moscow(code) ||
                        Boykonur(code);
      }

      private static bool Boykonur(String code)
      {
         return code.Equals("9900000000000");
      }

      private static bool Moscow(String code)
      {
         return code.Equals("7700000000000");
      }

      private static bool SanktPetersburg(String code)
      {
         return code.Equals("7800000000000");
      }

      private TreeNode FindNode(string code)
      {
         TreeNodeCollection nodes = tvKladr.Nodes;
         TreeNode result = null;

         foreach (TreeNode node in nodes)
         {
            KladrTreeNode ktn = node as KladrTreeNode;

            if (ktn != null && ktn._object.code.Equals(code))
            {
               result = node;
               break;
            }
            else
            {
               result = FindSubNode(node, code);

               if (result != null)
                  break;
            }
         }

         return result;
      }

      private TreeNode FindSubNode(TreeNode node, string code)
      {
         TreeNode result = null;

         foreach (TreeNode tn in node.Nodes)
         {
            KladrTreeNode ktn = tn as KladrTreeNode;

            if (ktn != null && ktn._object.code.Equals(code))
            {
               result = tn;
               break;
            }
            else if (tn.Nodes.Count > 0)
            {
               result = FindSubNode(tn, code);

               if (result != null)
                  break;
            }
         }

         return result;
      }

      private TreeNode selectedNode = null;

      private void tvKladr_MouseDown(object sender, MouseEventArgs e)
      {
         TreeNode selNode = tvKladr.GetNodeAt(e.X, e.Y);
         KladrTreeNode node = selNode as KladrTreeNode;
         tvKladr.SelectedNode = selNode;
         dgvStreet.DataSource = new List<Street>();
         selectedNode = tvKladr.SelectedNode;

         if (node != null)
         {
            KladrTreeNode ktn = (KladrTreeNode)node;
            string code = ktn._object.code;

            if (ktn._object.parent == null && 
                  node.Nodes.Count == 0)
               loadCities(code);
            else
            {
               if (ktn._object.streets.Count == 0)
                  loadStreets(code);
               else
               {
                  dgvStreet.DataSource = ktn._object.streets;
                  dgvStreet.Update();
               }
            }
            
            dgvStreet_SelectionChanged(null, null);
            addrSel.kladr = code;
         }
      }

      private void loadStreets(string code)
      {
         if (code.Length > 2)
         {
            //tvKladr.SelectedNode = FindNode(code);
            string ssCode = code.Substring(0, 11);

            DbCommand selectStreet = conn.CreateCommand();
            selectStreet.CommandText = String.Format("select code, name, socr from street where [code] like '{0}%00'", ssCode);
            DbDataReader reader = selectStreet.ExecuteReader();

            UpdateStreets(reader);

            if (addrSel.street.Length > 0)
            {
               foreach (DataGridViewRow row in dgvStreet.Rows)
               {
                  Street street = (Street)row.DataBoundItem;

                  if (street.code.ToUpper().Contains(addrSel.street.ToUpper()))
                  {
                     dgvStreet.Rows[row.Index].Cells[0].Selected = true;
                     dgvStreet_SelectionChanged(null, null);
                     return;
                  }
               }
            }
         }
      }

      void UpdateStreets(DbDataReader reader)
      {
         if (tvKladr.SelectedNode != null)
         {
            List<Street> streets = new List<Street>();

            while (reader.Read())
            {
               Street street = new Street();
               street.code = reader.GetString(0);
               street.name = reader.GetString(1);
               street.socr = reader.GetString(2);
               streets.Add(street);
            }

            streets.Sort(new Comparison<Street>(delegate (Street s1, Street s2){return s1.Name.CompareTo(s2.Name);}));

            ((KladrTreeNode)tvKladr.SelectedNode)._object.streets = streets;

            dgvStreet.DataSource = streets;
            dgvStreet.Update();
         }
      }

      private void dgvStreet_SelectionChanged(object sender, EventArgs e)
      {
         if (dgvStreet.CurrentRow != null &&
            tvKladr.SelectedNode != null)
         {
            Street street = (Street)dgvStreet.CurrentRow.DataBoundItem;
            StringBuilder result = new StringBuilder((
               (KladrTreeNode)tvKladr.SelectedNode).GetPath());
            result.Append(" ").Append(street.socr).Append(". ").Append(street.name);

            tbAddress.Text = result.ToString();
         }else if (tvKladr.SelectedNode != null)
            tbAddress.Text = ((KladrTreeNode)tvKladr.SelectedNode).GetPath();
      }

      private void tbFind_KeyPress(object sender, KeyPressEventArgs e)
      {
         string toFind = tbFind.Text + e.KeyChar;

         foreach (DataGridViewRow row in dgvStreet.Rows)
         {
            Street street = (Street)row.DataBoundItem;

            if (street.name.ToUpper().Contains(toFind.ToUpper()))
            {
               dgvStreet.Rows[row.Index].Cells[0].Selected = true;
               dgvStreet_SelectionChanged(null, null);
               return;
            }
         }
      }

      private void btnCancel_Click(object sender, EventArgs e)
      {
         Close();
      }

      private void btnOK_Click(object sender, EventArgs e)
      {
         Close();
      }

      private void FmKladr_FormClosing(object sender, FormClosingEventArgs e)
      {
         DataGridViewRow row = dgvStreet.CurrentRow;

         if (row != null)
         {
            Street street = row.DataBoundItem as Street;

            if (street != null)
               addrSel.street = street.code;
         }

         if (DialogResult == DialogResult.OK && isOK != null)
            e.Cancel = isOK(Address) != true;
      }

      private void tbNumberHome_KeyPress(object sender, KeyPressEventArgs e)
      {
         if (!e.KeyChar.Equals('\r'))
         {
            string text = tbNumberHome.Text + e.KeyChar;
            StringBuilder result = new StringBuilder();

            DataGridViewRow row = dgvStreet.CurrentRow;

            if(row != null)
            {
               Street street = row.DataBoundItem as Street;

               if (street != null)
               {
                  result.Append((
                     (KladrTreeNode)tvKladr.SelectedNode).GetPath())
                        .Append(" ").Append(street.socr).Append(". ").Append(street.name).Append(", ").Append(text);
               }

            }else
               result.Append((
                     (KladrTreeNode)tvKladr.SelectedNode).GetPath()).Append(text);

            tbAddress.Text = result.ToString();
         }
      }

      public string Address { get { return tbAddress.Text; } }
   }

   class KladrObject
   {
      public string code = "";
      public string name = "";
      public string socr = "";
      public KladrObject parent;
      public List<KladrObject> items = new List<KladrObject>();
      public List<Street> streets = new List<Street>();

      public override string ToString()
      {
         return String.Format("{0} {1}", socr, name);
      }

      public override bool Equals(object obj)
      {
         return code.Equals(((KladrObject)obj).code);
      }

      public override int GetHashCode()
      {
         return base.GetHashCode();
      }
   }

   class KladrTreeNode : TreeNode
   {
      public KladrObject _object;

      public KladrTreeNode(KladrObject _obj)
         : base(_obj.ToString())
      {
         _object = _obj;
      }

      public string GetPath()
      {
         KladrObject parent = _object;
         String result = string.Empty;

         do
         {
            result = parent.parent == null ?
               String.Format("{0} {1}., {2}", parent.name, parent.socr, result)
               : String.Format("{0}. {1}, {2}", parent.socr, parent.name, result);
            parent = parent.parent;
         } while (parent != null);

         return result;
      }
   }

   class KladrTree
   {
      public List<KladrObject> items = new List<KladrObject>();

      public void Load(DbDataReader reader, TreeView control, KladrTreeNode node)
      {
         KladrObject ss = node == null ? new KladrObject() : node._object;
         KladrObject reg = new KladrObject();
         KladrObject city = new KladrObject();
         KladrObject local = new KladrObject();

         while (reader.Read())
         {
            Kladr kladr = new Kladr();
            kladr.code = reader.GetString(0);
            kladr.name = reader.GetString(1);
            kladr.socr = reader.GetString(2);

            string curKey = kladr.code;
            string level0 = curKey.Substring(0, 2);
            string level1 = curKey.Substring(2, 3);
            string level2 = curKey.Substring(5, 3);
            string level3 = curKey.Substring(8, 3);

            if (ss.code.Equals(string.Empty) || !ss.code.Substring(0, 2).Equals(level0))
            {
               KladrObject testObject = new KladrObject();
               testObject.code = kladr.code;

               if (!items.Contains(testObject))
               {
                  ss = new KladrObject();
                  ss.code = kladr.code;
                  ss.name = kladr.name;
                  ss.socr = kladr.socr;
                  ss.parent = null;

                  items.Add(ss);
               }
               else
                  ss = items[items.IndexOf(testObject)];

               reg = new KladrObject();
               city = new KladrObject();
               local = new KladrObject();
            }
            else if (!level1.Equals("000") && (reg.code.Length == 0 ? true :
               !reg.code.Substring(2, 3).Equals(level1)))
            {
               reg = new KladrObject();
               reg.code = kladr.code;
               reg.name = kladr.name;
               reg.socr = kladr.socr;
               reg.parent = ss;

               ss.items.Add(reg);

               city = new KladrObject();
               local = new KladrObject();
            }
            else if (!level2.Equals("000") && (city.code.Length == 0 ? true : 
               !city.code.Substring(5, 3).Equals(level2)))
            {
               city = new KladrObject();
               city.code = kladr.code;
               city.name = kladr.name;
               city.socr = kladr.socr;

               if (level1.Equals("000")){
                  city.parent = ss;
                  ss.items.Add(city);
               }
               else 
               {
                  city.parent = reg;
                  reg.items.Add(city);
               }

               local = new KladrObject();
            }
            else if (!level3.Equals("000") && (local.code.Length == 0 ? true : 
               !local.code.Substring(8, 3).Equals(level3)))
            {
               local = new KladrObject();
               local.code = kladr.code;
               local.name = kladr.name;
               local.socr = kladr.socr;

               if (!level2.Equals("000"))
               {
                  local.parent = city;
                  city.items.Add(local);
               } 
               else if (!level1.Equals("000"))
               {
                  local.parent = reg;
                  reg.items.Add(local);
               }
               else 
               {
                  local.parent = ss;
                  ss.items.Add(local);
               }
            }
         }

         control.BeginUpdate();

         if (node != null)
         {
            foreach (KladrObject item in items)
            {
               if (item.code.Equals(node._object.code))
               {
                  foreach (KladrObject ko in item.items)
                  {
                     loadControl(control, ko, node);
                  }

                  break;
               }
            }
         }
         else
            foreach (KladrObject item in items)
            {
               
               KladrTreeNode ktn = new KladrTreeNode(item);
               control.Nodes.Add(ktn);

               foreach (KladrObject ko in item.items)
               {
                  loadControl(control, ko, ktn);
               }
            }

         control.EndUpdate();
      }

      private void loadControl(TreeView control, 
         KladrObject obj, KladrTreeNode parent)
      {
         KladrTreeNode ktn = new KladrTreeNode(obj);
         parent.Nodes.Add(ktn);

         foreach (KladrObject ko in obj.items)
         {
            loadControl(control, ko, ktn);
         }
      }
   }
}
