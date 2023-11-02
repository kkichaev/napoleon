using GRSoft.Network;
using System.Windows.Forms;
using System.Collections;
using System.Collections.Generic;

namespace GRSoft.NapoleonManager
{
   class DivisionFormEx : DivisionForm
   {
      DataGridViewCheckBoxColumn whColumn = new DataGridViewCheckBoxColumn();
      List<CommonConfig> updated = new List<CommonConfig>();
      const string TOTA_ORG_EDIT_KEY = "TOTAL_ORG_EDIT";

      public DivisionFormEx() : base()
      {
         childUserList.CurrentCellDirtyStateChanged += new System.EventHandler(CurrentCellDirtyStateChanged);

         whColumn.DataPropertyName = "FullOrgEditAllowed";
         whColumn.HeaderText = "Редактор новой точки";
         whColumn.Name = "fullorgedit";
         whColumn.Width = 65;

         childUserList.Columns.Add(whColumn);
      }

      void CurrentCellDirtyStateChanged(object sender, System.EventArgs e)
      {
         DataGridViewCell cell = childUserList.CurrentCell;
         if (cell != null && childUserList.Columns[cell.ColumnIndex].HeaderText == whColumn.HeaderText)
         {
            childUserList.CommitEdit(DataGridViewDataErrorContexts.Commit);
         }
      }

      internal bool IsTotalOrgEdit(Agent a)
      {
         foreach (CommonConfig c in updated)
         {
            if (c.userid == a.id)
               return (int.Parse(c.value) == 1);
         }

         CommonConfig cc = ConfigUtils.GetConfig(dsCommonConfig, new ConfigKeyItems(TOTA_ORG_EDIT_KEY), a);
         if( cc == null )
            return false;

         return (int.Parse(cc.value) == 1);
      }

      internal override bool BeforeWriteChanges(List<IDataSet> wrObj, List<IDataSet> rmvObj, List<ReplacedSet> replaced, DBConnection conn)
      {
         bool ret = true;

         if (updated.Count > 0)
         {
            int index = 0;
            DataSet<int, CommonConfig> ins = new DataSet<int, CommonConfig>(CommonConfig.OBJECT_NAME, false);
            foreach (CommonConfig c in updated)
               ins.Add(index++, c);

            wrObj.Add(ins);
         }

         return ret;
      }

      internal override void AfterWrited()
      {
         foreach (CommonConfig c in updated)
            ConfigUtils.AddConfig(dsCommonConfig, c);
         updated.Clear();
      }

      internal void SetTotalOrgEdit(Agent a, bool canChange)
      {
         if (parent != null)
         {
            string value = (canChange) ? "1" : "0";
            foreach (CommonConfig c in updated)
            {
               if (c.userid.Equals(a.id))
               {
                  c.value = value;
                  return;
               }
            }

            CommonConfig cc = new CommonConfig();
            cc.userid = a.id;
            cc.value = value;
            cc.key = TOTA_ORG_EDIT_KEY;
            updated.Add(cc);

            parent.MarkChanged();
         }
      }

      protected override DivisionForm.DataItem CreateItem(Agent a, DivisionForm form)
      {
         return new DataItemEx(a, form);
      }

      class DataItemEx : DivisionForm.DataItem
      {
         public DataItemEx(Agent a, DivisionForm o) : base(a, o)
         {
         }

         public bool FullOrgEditAllowed
         {
            get
            {
               return ((DivisionFormEx)owner).IsTotalOrgEdit(agent);
            }

            set
            {
               ((DivisionFormEx)owner).SetTotalOrgEdit(agent, value);
            }
         }
      }

      private void InitializeComponent()
      {
         this.SuspendLayout();
         // 
         // DivisionFormEx
         // 
         this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
         this.Name = "DivisionFormEx";
         this.Size = new System.Drawing.Size(1310, 530);
         this.ResumeLayout(false);
         this.PerformLayout();

      }

      
   }
}