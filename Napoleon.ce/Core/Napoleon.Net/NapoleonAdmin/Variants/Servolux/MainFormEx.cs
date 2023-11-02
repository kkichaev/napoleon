using GRSoft.Network;
using System.Windows.Forms;

namespace GRSoft.NapoleonAdmin
{
   public class MainFormEx : MainForm
   {
      DataSet<string, AdmRequestSync> dsReqSync = new DataSet<string, AdmRequestSync>(AdmRequestSync.OBJECT_NAME);
      DataSet<string, AdmRequestSync> dsReqSyncWr = new DataSet<string, AdmRequestSync>(AdmRequestSync.OBJECT_NAME);

      DataGridViewCheckBoxColumn clmnCanSend;
      DataGridViewCheckBoxColumn clmnCanDisableFirms;
      //DataGridViewCheckBoxColumn clmnCanCopyOrder;
      DataGridViewCheckBoxColumn clmnCanCopyOrder;
      DataGridViewCheckBoxColumn clmnCanChangeOrder;
      DataGridViewCheckBoxColumn clmnCanChangeRoute;
      DataGridViewCheckBoxColumn clmnReqSync;

      DataGridViewCheckBoxColumn[] rightColumns;

      public MainFormEx()
      {
         clmnCanDisableFirms = new DataGridViewCheckBoxColumn();
         clmnCanDisableFirms.DataPropertyName = "CanDisableFirms";
         clmnCanDisableFirms.HeaderText = "Блокировка заявок по фирме";
         clmnCanDisableFirms.Name = "clmnCanDisableFirms";
         clmnCanDisableFirms.Visible = false;
         clmnCanDisableFirms.Width = 90;

         clmnCanSend = new DataGridViewCheckBoxColumn();
         clmnCanSend.DataPropertyName = "CanSendOrders";
         clmnCanSend.HeaderText = "Может отправлять заявки";
         clmnCanSend.Name = "clmnCanSend";
         clmnCanSend.Visible = false;
         clmnCanSend.Width = 90;

         //clmnReturnViewRigth = new DataGridViewCheckBoxColumn();
         //clmnReturnViewRigth.DataPropertyName = "ReturnViewRigth";
         //clmnReturnViewRigth.HeaderText = "Может просматривать возвраты";
         //clmnReturnViewRigth.Name = "clmnReturnViewRigth";
         //clmnReturnViewRigth.Visible = false;
         //clmnReturnViewRigth.Width = 90;

         clmnCanCopyOrder = new DataGridViewCheckBoxColumn();
         clmnCanCopyOrder.DataPropertyName = "CopyOrderRight";
         clmnCanCopyOrder.HeaderText = "Копировать заявки";
         clmnCanCopyOrder.Name = "clmnCanCopyOrder";
         clmnCanCopyOrder.Visible = false;
         clmnCanCopyOrder.Width = 90;

         clmnCanChangeRoute = new DataGridViewCheckBoxColumn();
         clmnCanChangeRoute.DataPropertyName = "CanChangeRoute";
         clmnCanChangeRoute.HeaderText = "Разрешить корректировку маршрута";
         clmnCanChangeRoute.Name = "clmnCanChangeRoute";
         clmnCanChangeRoute.Visible = false;
         clmnCanChangeRoute.Width = 90;

         clmnCanChangeOrder = new DataGridViewCheckBoxColumn();
         clmnCanChangeOrder.DataPropertyName = "CanChangeOrder";
         clmnCanChangeOrder.HeaderText = "Разрешить подрезку";
         clmnCanChangeOrder.Name = "clmnCanChangeOrder";
         clmnCanChangeOrder.Visible = false;
         clmnCanChangeOrder.Width = 90;

         clmnReqSync = new DataGridViewCheckBoxColumn();
         clmnReqSync.DataPropertyName = "RequestSync";
         clmnReqSync.HeaderText = "Синхронизация!";
         clmnReqSync.Name = "clmnReqSync";
         clmnReqSync.Visible = false;
         clmnReqSync.Width = 90;

         rightColumns = new DataGridViewCheckBoxColumn[] { clmnCanDisableFirms,
            clmnCanSend, clmnCanChangeRoute, clmnCanChangeOrder,
            clmnReqSync, clmnCanCopyOrder };
         usersView.Columns.AddRange(rightColumns);

         Width += 290;
      }

      protected override void RefreshUserData()
      {
         base.RefreshUserData();
         
         foreach(UserDataItem udi in userData)
         {
            AdmRequestSync val;
            if(dsReqSync.TryGetValue(udi.Id, out val) && val.sync > 0)
            {
               ((UserDataItemEx)udi).RequestSync = true;
            }
         }
      }

      protected override void AddUpdDataSet(System.Collections.Generic.List<IDataSet> upd)
      {
         upd.Add(dsReqSync);
      }

      public override void BeforeUpdate(System.Collections.Generic.List<IDataSet> wr, System.Collections.Generic.List<IDataSet> rmv)
      {
         if (dsReqSyncWr.Count > 0)
            wr.Add(dsReqSyncWr);
      }

      public override void OnUpdate(bool res)
      {
         if(res)
         {
            foreach (AdmRequestSync ars in dsReqSyncWr.Data)
               dsReqSync[ars.userid] = ars;

            dsReqSyncWr.Clear();
         }
      }

      protected override void OnUserDataChanging(Resolver resolver)
      {
         base.OnUserDataChanging(resolver);
         if (resolver.Field == "RequestSync")
         {
            if((bool)resolver.NewValue)
            {
               AdmRequestSync ars = new AdmRequestSync();
               ars.userid = resolver.item.Id;
               ars.sync = 1;
               dsReqSyncWr[resolver.item.Id] = ars;
            }
            else
            {
               dsReqSyncWr.Remove(resolver.item.Id);
            }
         }
      }

      protected override void usersView_CurrentCellDirtyStateChanged(object sender, System.EventArgs e)
      {
         base.usersView_CurrentCellDirtyStateChanged(sender, e);
         foreach(DataGridViewCheckBoxColumn c in rightColumns)
            if (usersView.CurrentCell.ColumnIndex == c.DisplayIndex)
            {
               usersView.CommitEdit(DataGridViewDataErrorContexts.Commit);
               //if (usersView.CurrentCell.ColumnIndex == clmnReqSync.DisplayIndex)
               //{
               //   UserDataItemEx udi = usersView.Rows[usersView.CurrentCell.RowIndex].DataBoundItem as UserDataItemEx;
               //   if ((bool)usersView.CurrentCell.Value)
               //   {
               //      AdmRequestSync ars = new AdmRequestSync();
               //      ars.userid = udi.Id;
               //      ars.sync = 1;
               //      dsReqSyncWr[udi.Id] = ars;
               //      userChangesSave.Enabled = true;
               //   } else
               //   {
               //      dsReqSyncWr.Remove(udi.Id);
               //   }
               //}
               break;
            }
      }

      protected override void PrepareViewComponents(bool agentView)
      {
         base.PrepareViewComponents(agentView);

         tracking.Visible = agentView;

         if (clmnCanSend != null)
         {
            clmnCanDisableFirms.Visible = !agentView;
            clmnCanSend.Visible = !agentView;
            clmnCanChangeOrder.Visible = !agentView;
            clmnCanChangeRoute.Visible = !agentView;

            clmnReqSync.Visible = agentView;
            clmnCanCopyOrder.Visible = agentView;
         }
      }
   }
}