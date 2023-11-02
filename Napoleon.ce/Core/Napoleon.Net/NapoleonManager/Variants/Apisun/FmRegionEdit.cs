using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public partial class FmRegionEdit : Form
   {
      public FmRegionEdit()
      {
         InitializeComponent();
      }

      public static OrgRegion EditRegion(OrgRegion region)
      {
         FmRegionEdit form = new FmRegionEdit();
         OrgRegion result = null;

         if (region != null)
         {
            form.tbName.Text = region.name;
         }

         if (form.ShowDialog() == DialogResult.OK)
         {
            result = region ?? new OrgRegion();

            if (region == null)
               result.id = GRSoft.Network.DataObject.GenId();

            result.name = form.tbName.Text.Trim();
         }

         return result;
      }

      private void FmRegionEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK)
         {
            if (tbName.Text.Trim().Length == 0)
            {
               tbName.Focus();
               MessageBox.Show("Поле не может быть пустым",
                      "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
               e.Cancel = true;
            }
         }
      }
   }

   public class OrgRegion : GRSoft.Network.DataObject, TreeData
   {
      public static readonly string OBJECT_NAME = "OrgRegion";

      [KeyField]
      public string id = string.Empty;
      public string name = string.Empty;
      public string parent = string.Empty;

      #region ColumnsData Members

      public string[] Data
      {
         get { return new string[] { name }; }
      }

      #endregion

      #region ColumnsData Members


      public string Id
      {
         get { return id; }
      }

      #endregion

      #region TreeData Members


      public string Parent
      {
         get { return parent; }
      }

      #endregion

      public override string ToString()
      {
         return name;
      }

      public override int GetHashCode()
      {
         return base.GetHashCode();
      }
   }
}
