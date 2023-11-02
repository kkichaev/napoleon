using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class FmMatrixDesignerEx : FmMatrixDesigner
   {
      static FmMatrixDesignerEx instance = null;

      string contractId = "";

      public static void Open(string contraciId)
      {
         if( instance == null )
         {
            instance = new FmMatrixDesignerEx();
            instance.contractId = contraciId;
            instance.Show();
         }
         else
         {
            instance.BringToFront();
            instance.RefreshData();
         }
      }

      protected override void OnLoad(EventArgs e)
      {
         base.OnLoad(e);
         RefreshData();
      }

      protected override void OnClosed(EventArgs e)
      {
         base.OnClosed(e);
         instance = null;
      }

      protected override void RefreshData()
      {
         dsMatrix.Filter = "\"cdef\" = '" + contractId + "'";
         dsPrice.Filter = "\"cdef\" = '" + contractId + "'";

         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsPrice);
         upd.Add(dsMatrix);

         FmWait.StdDataRefresh(this, upd, DoLoadData, tsbRefresh);
      }

      void DoLoadData()
      {
         dsFolder.Clear();
         foreach (Price p in dsPrice.Data)
         {
            p.fid = p.group;
            if (dsFolder.ContainsKey(p.group) == false)
            {
               ManagerFolder mf = new ManagerFolder();
               mf.id = p.group;
               mf.name = p.group;
               mf.level = 0;
               dsFolder[mf.id] = mf;
            }
         }

         ControlsFillAfterLoaded();
      }

      protected override bool SaveData()
      {
         SimpleDataSet<Matrix> rmv = new SimpleDataSet<Matrix>(Matrix.OBJECT_NAME, false);
         DataSet<int, Matrix> curMatrix = GetMatrixDataSet();

         Dictionary<string, bool> cv = new Dictionary<string, bool>();
         foreach(Matrix m in curMatrix.Data)
         {
            m.cdef = contractId;
            cv.Add(m.name, true);
         }

         foreach(Matrix m in dsMatrix.Data)
         {
            m.cdef = contractId;
            if (!cv.ContainsKey(m.name))
               rmv.Add(m);
         }

         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(curMatrix);

         List<IDataSet> rmvSet = new List<IDataSet>();
         rmvSet.Add(rmv);

         bool ret = DataModule.UpdateDataSet(upd, rmvSet, null, Config.GetConfig().GetConnection());
         if( ret )
            dsMatrix = curMatrix;
         return ret;
      }
   }
}
