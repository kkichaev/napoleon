using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class FmExportPhotoEx : FmExportPhoto
   {
      private ComboBox cbDistrib;
      private DataSet<string, TypeDistrib> dsTDistr;
      private object selTypeDistr = null;
 
      public FmExportPhotoEx()
      {
         cbDistrib = new ComboBox();
         cbDistrib.Location = new  Point(60, 330);
         cbDistrib.Size = new Size(275, 18);
         Controls.Add(cbDistrib);

         dsTDistr = (DataSet<string, TypeDistrib>)DataModule.Get(TypeDistrib.OBJECT_NAME) ?? new DataSet<string, TypeDistrib>(TypeDistrib.OBJECT_NAME);

         Size = new Size(Size.Width, Size.Height + 60);

         Label lbl = new Label();
         lbl.Text = "Типы дистрибуций";
         lbl.Size = new Size(200, 18);
         lbl.Location = new Point(37, 310);

         Controls.Add(lbl);

         Load += FmExportPhotoEx_Load;

         cbDistrib.SelectedIndexChanged += cbDistrib_SelectedIndexChanged;
      }

      void cbDistrib_SelectedIndexChanged(object sender, EventArgs e)
      {
         selTypeDistr = ((ComboBox)sender).SelectedItem;
      }

      void FmExportPhotoEx_Load(object sender, EventArgs e)
      {
         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsTDistr);

         FmWait.StdDataRefresh(this, list, DoLoadData);
      }

      private void DoLoadData()
      {
         List<TypeDistrib> list = new List<TypeDistrib>();
         list.AddRange(dsTDistr.Values);
         list.Sort((x, y) => { return x.text.CompareTo(y.text); });

         cbDistrib.Items.Add("<Все>");
         cbDistrib.Items.AddRange(list.ToArray());
         cbDistrib.SelectedIndex = 0;
      }

      protected override bool CheckItem(Visit.VisitItem item)
      {
         TypeDistrib t = selTypeDistr as TypeDistrib;
         return (t == null || t.id.Equals(item.did)) && base.CheckItem(item);
      }
   }
}
