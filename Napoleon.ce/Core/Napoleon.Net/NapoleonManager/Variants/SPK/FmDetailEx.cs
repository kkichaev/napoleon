using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public class FmDetailEx : FmDetail
   {
      protected DataSet<int, Layout> dsLayout;
      Dictionary<DateTime, LayoutApprove> approved = null;
      protected DataSet<int, LayoutApprove> dsApproveLayout;
      public SimpleDataSet<LayoutActionCause> causes;

      private Button btnApplay = new Button();
      private LayoutDetail layoutDetail = new LayoutDetail(), scriptLayout = new LayoutDetail();
      int countLayout = 0;
      DataSet<string, OrgDisablePhoto> dsDisabled = new DataSet<string, OrgDisablePhoto>(OrgDisablePhoto.OBJECT_NAME, false);

      bool isDisplayOperator = false, isStuff = false, isSU = false;

      public FmDetailEx(FmDetailData data)
         : base(data)
      {
         dsLayout = (DataSet<int, Layout>)DataModule.Get(GRSoft.NapoleonManager.Layout.OBJECT_NAME) ?? new DataSet<int, Layout>(GRSoft.NapoleonManager.Layout.OBJECT_NAME);
         dsApproveLayout = (DataSet<int, LayoutApprove>)DataModule.Get(GRSoft.NapoleonManager.LayoutApprove.OBJECT_NAME_APPROVE) ?? new DataSet<int, LayoutApprove>(LayoutApprove.OBJECT_NAME_APPROVE);
         causes = new SimpleDataSet<LayoutActionCause>(LayoutActionCause.OBJECT_NAME);

         documents.Add(new DocumentInfo(dsLayout, ObjType.TObjType.Layout));

         layoutDetail.SetOwner(this);
         layoutDetail.Visible = false;
         layoutDetail.Dock = DockStyle.Fill;
         detailPanel.Controls.Add(layoutDetail);

         Manager m = CurrentUser.user as Manager;
         isDisplayOperator = m.HaveRight(RightTokens.Get("DisplayChecker"), RightActions.Write);
         isStuff = m.HaveRight(RightTokens.Get("Stuff"), RightActions.Write);
         isSU = !isDisplayOperator && !isStuff;

         layoutDetail.EnableWriteData((isSU || isDisplayOperator));

         //btnApplay.Name = "bntApply";
         //btnApplay.Text = "Утвердить (без изменений)";
         //btnApplay.Location = new Point(pnlNotes.Left + pnlNotes.Width - btnApplay.Width - 2, 2);
         //btnApplay.Anchor = AnchorStyles.Top | AnchorStyles.Right;
         //btnApplay.BackColor = Color.LightGray;
         //btnApplay.ForeColor = System.Drawing.Color.Black;
         //btnApplay.Visible = false;
         //btnApplay.Click += (x,e) => ApproveLayout();

         //pnlNotes.Height = 40;
         //pnlNotes.Controls.Add(btnApplay);
      }

      public Dictionary<DateTime, LayoutApprove> Approved
      {
         get
         {
            if( approved == null)
            {
               approved = new Dictionary<DateTime, LayoutApprove>();
               foreach (LayoutApprove i in dsApproveLayout.Data)
                  approved.Add(i.created, i);
            }
            return approved;
         }
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         string flt = string.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsLayout.Filter = flt;
         dsApproveLayout.Filter = flt;

         dsDisabled.Command = new ServerCommand(Commands.Impersonate(Commands.GET, agentID), dsDisabled.Name);

         updSets.Add(dsLayout);
         updSets.Add(dsApproveLayout);
         updSets.Add(causes);
         updSets.Add(dsDisabled);

         base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);
      }

      class DocViewEx : DocView
      {
         FmDetailEx owner;
         bool enableWrite;
         LayoutDetail ld;

         public DocViewEx(String type, String title, Type viewer, FmDetailEx owner, bool enableWrite, LayoutDetail ld)
            : base(type, title, viewer)
         {
            this.owner = owner;
            this.enableWrite = enableWrite;
            this.ld = ld;
         }

         public override Control MakeControl()
         {
            //LayoutDetail ld = (LayoutDetail)base.MakeControl();
            //ld.SetOwner(owner);
            //ld.EnableWriteData(enableWrite);
            ld.SetData(null);
            ld.Visible = true;
            return ld;
         }
      }

      protected override FmDetail.DocView GetDocView(string docType)
      {
         FmDetail.DocView result = null;

         if (docType.Equals(GRSoft.NapoleonManager.Layout.OBJECT_NAME))
            result = new DocViewEx(GRSoft.NapoleonManager.Layout.OBJECT_NAME, "Выкладка", typeof(LayoutDetail), this, (isSU || isDisplayOperator), layoutDetail);
         else
            result = base.GetDocView(docType);

         return result;
      }

      internal override Control RefreshDetail(OrderDetailRepresentation odr)
      {
         Control result = base.RefreshDetail(odr);

         if (odr.StoreObject is Layout)
         {
            layoutDetail.SetData(odr.StoreObject as Layout);

            //layoutDetail.EnableWriteData(isSU || isDisplayOperator);
            result = layoutDetail;  
         }

         return result;
      }

      protected override void ShowCorrespondingPhoto(DateTime date, OrderDetailRepresentation o)
      {
         base.ShowCorrespondingPhoto(date, o);
         if(lvPhoto.Items.Count == 0 && o.NOrg != null && dsDisabled.ContainsKey(o.NOrg.id))
         {
            Bitmap img = GRSoft.NapoleonManager.Properties.Resources.disable_photo;
            imPhoto.Images.Add(img);

            ListViewItem lvi = lvPhoto.Items.Add("запрет на съемку");
            lvi.ImageIndex = 0;

            List<Image> pics = new List<Image>();
            pics.Add(img);
            imPhoto.Tag = pics;
         }
      }

      protected override string TotalCount()
      {
         return base.TotalCount() + " Выкладок: " + countLayout.ToString();
      }

      protected override void AfterRefreshData()
      {
         base.AfterRefreshData();

         countLayout = 0;
         //List<int> needRemove = new List<int>();
         foreach(KeyValuePair<int, Layout> kv in dsLayout)
         {
            //if (kv.Value.IsEmpty)
            //   needRemove.Add(kv.Key);
            //else
               countLayout++;
         }

         //needRemove.ForEach(x => dsLayout.Remove(x));

         approved = null;
      }

      void MoveToBack()
      {
         if (layoutDetail.RowIndex >= 0)
            dgvDetail.CurrentCell = dgvDetail.Rows[layoutDetail.RowIndex].Cells[dgvDetailColumnOrg.DisplayIndex];
      }

      bool CheckChanges(bool canCancel)
      {
         DialogResult dr = System.Windows.Forms.DialogResult.OK;
         if (layoutDetail.IsDirty())
         {
            dr = MessageBox.Show("Сохранить изменения?", "Вопрос",
               canCancel ? MessageBoxButtons.YesNoCancel : MessageBoxButtons.YesNo, MessageBoxIcon.Question);
         }
         if (dr == System.Windows.Forms.DialogResult.Yes)
            layoutDetail.SaveChanges();
         if(dr == System.Windows.Forms.DialogResult.No)
            layoutDetail.Deattach();
         return dr != System.Windows.Forms.DialogResult.Cancel;
      }

      protected override void OnClosing(System.ComponentModel.CancelEventArgs e)
      {
         if (!CheckChanges(true))
         {
            e.Cancel = true;
            return;
         }
         base.OnClosing(e);
      }

      protected override void btnRefresh_Click_1(object sender, EventArgs e)
      {
         if (!CheckChanges(true))
            return;
         base.btnRefresh_Click_1(sender, e);
      }

      protected override void cbAgents_SelectionChangeCommitted(object sender, EventArgs e)
      {
         CheckChanges(false);
         base.cbAgents_SelectionChangeCommitted(sender, e);
      }

      protected override void miModeDoc_Click(object sender, EventArgs e)
      {
         if (!CheckChanges(true))
            return;
         base.miModeDoc_Click(sender, e);
      }

      protected override void miModeScript_Click(object sender, EventArgs e)
      {
         if (!CheckChanges(true))
            return;
         base.miModeScript_Click(sender, e);
      }

      protected override void UpdateDetailTable(DataGridViewRow curRow)
      {
         if (curRow != null && curRow.Index == layoutDetail.RowIndex) 
            return;

         if (curRow != null
            && curRow.DataBoundItem is OrderDetailRepresentation
            && !(((OrderDetailRepresentation)curRow.DataBoundItem).StoreObject is BaseDocument))
         {
            wbPhoto.DocumentText = "<html></html>";
            assignedHtml = string.Empty;
         }

         if (!CheckChanges(true))
         {
            BeginInvoke((EmptyParamHandler)MoveToBack);
            return;
         }

         layoutDetail.RowIndex = -1;
         layoutDetail.GridRowIndex = (curRow == null) ? -1 : curRow.Index;

         base.UpdateDetailTable(curRow);
      }

      //private void ApproveLayout()
      //{
      //   if (dgvDetail.CurrentRow != null)
      //   {
      //      OrderDetailRepresentation odr = dgvDetail.CurrentRow.DataBoundItem as OrderDetailRepresentation;

      //      if (odr != null && odr.StoreObject is GRSoft.NapoleonManager.Layout)
      //      {
      //         List<IDataSet> update = new List<IDataSet>();

      //         DataSet<int, Layout> ds  = new DataSet<int,Layout>(GRSoft.NapoleonManager.Layout.OBJECT_NAME_APPROVED, false);
      //         ds.Add(ds.Count, odr.StoreObject);
      //         update.Add(ds);

      //         Config cfg = Config.GetConfig();

      //         if (DataModule.UpdateDataSet(update, null, null, cfg.GetConnection(), GetSelectedIdAgent()))
      //         {
      //            MessageBox.Show("Операция завершена успешно", "Информация", MessageBoxButtons.OK,
      //               MessageBoxIcon.Information);
      //            approved.Add(((GRSoft.NapoleonManager.Layout)odr.StoreObject).created);
      //            dgvDetail.Invalidate();
      //         }
      //         else
      //            MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK,
      //               MessageBoxIcon.Error);
      //      }
      //   }
      //}

      protected override void CellFormatting(DataGridViewCellFormattingEventArgs e)
      {
         base.CellFormatting(e);

         GRSoft.Network.DataObject dataObject = (dgvDetail.Rows[e.RowIndex].DataBoundItem as OrderDetailRepresentation).StoreObject;

         if (dataObject is GRSoft.NapoleonManager.Layout)
         {
            GRSoft.NapoleonManager.Layout layout = (GRSoft.NapoleonManager.Layout)dataObject;

            if (layoutDetail.IsApproved(layout))
            {
               e.CellStyle.BackColor = System.Drawing.Color.LightGreen;
            }
         }
      }

      protected string WinChar(string input)
      {
         string result = input;
         result = result.Replace('\\', '_').Replace('/', '_').Replace(':', '_').Replace('*', '_').Replace('?', '_')
            .Replace('"', '_').Replace('<', '_').Replace('>', '_').Replace('|', '_');

         return result;
      }

      protected override void ShowPhoto(Image photo, string tag)
      {
         if ( isStuff || isSU || (isDisplayOperator && layoutDetail.IsEditMode()))
         {
            if (dgvDetail.CurrentRow != null)
            {
               OrderDetailRepresentation odr = dgvDetail.CurrentRow.DataBoundItem as OrderDetailRepresentation;
               if (odr != null && odr.Org != null)
               {
                  //string name = string.Format("D:\\tmp_photo.png", Path.GetTempPath());
                  string name = string.Format("{0}{1}.png", Path.GetTempPath(), WinChar(odr.Org));
                  photo.Save(name, System.Drawing.Imaging.ImageFormat.Png);
                  System.Diagnostics.Process.Start(name);
               }
            }
         }
      }
   }

   
}
