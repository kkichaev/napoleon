using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;

namespace GRSoft.NapoleonManager
{
   public partial class FmMerchView : UserControl, DataObjectViewer
   {
      public FmMerchView()
      {
         InitializeComponent();
         grid1.AutoGenerateColumns = false;
         grid2.AutoGenerateColumns = false;
      }

      public void SetData(Network.DataObject obj)
      {
         Merchendizing doc = obj as Merchendizing;

         if (doc != null)
         {
            initFolders(doc);
            initItems(doc);
         }
      }

      private void initFolders(Merchendizing doc)
      {
         List<MerchFolderView> list = new List<MerchFolderView>();

         foreach (Merchendizing.MFolder f in doc.folders)
         {
            MerchFolderView v = new MerchFolderView();
            v.Name = f.item.name;
            v.Mine = f.mine;
            v.Their = f.their;

            list.Add(v);
         }

         list.Sort((x, y) => { return x.Name.CompareTo(y.Name); });
         BindingList<MerchFolderView> data = new BindingList<MerchFolderView>();
         int pos = 1;
         list.ForEach((i) => { i.Pos = pos++; data.Add(i); });
         grid1.DataSource = data;
      }

      private void initItems(Merchendizing doc)
      {
         List<MerchItemView> list = new List<MerchItemView>();

         foreach (Merchendizing.Item f in doc.items)
         {
            MerchItemView v = new MerchItemView();
            v.Name = f.item.name;
            v.Qty = f.qty;
            v.System = f.system;

            list.Add(v);
         }

         list.Sort((x, y) => { return x.Name.CompareTo(y.Name); });
         BindingList<MerchItemView> data = new BindingList<MerchItemView>();
         int pos = 1;
         list.ForEach((i) => { i.Pos = pos++; data.Add(i); });
         grid2.DataSource = data;
      }

      private class MerchFolderView
      {
         public int Pos { get; set; }
         public string Name { get; set; }
         public double Mine { get; set; }
         public double Their { get; set; }
      }

      private class MerchItemView
      {
         public int Pos { get; set; }
         public string Name { get; set; }
         public double Qty { get; set; }
         public double System { get; set; }
      }
   }
}
