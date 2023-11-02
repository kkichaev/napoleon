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
   public
   class FmDetailEx : FmDetail
   {
      SimpleDataSet<NapoleonTaskDoc> dsTaskDocs = new SimpleDataSet<NapoleonTaskDoc>("NapoleonTaskDoc", false);

      SimpleDataSet<NapoleonTask> dsTask = new SimpleDataSet<NapoleonTask>(NapoleonTask.OBJECT_NAME);
      SimpleDataSet<NapoleonTaskResponse> dsResponce = new SimpleDataSet<NapoleonTaskResponse>(NapoleonTaskResponse.OBJECT_NAME);

      DataSet<string, NapoleonOrderDogorvor> dsDogovors = new DataSet<string, NapoleonOrderDogorvor>(NapoleonOrderDogorvor.OBJECT_NAME);

      public FmDetailEx(FmDetailData data):base(data)
      {
         dgvDetailColumnOrg.HeaderText = "Контрагент/задача";

         //dgvDetailColumnOrg.Visible = false;
         //dgvDetailColumnDocType.Visible = false;
         //scBottom.Panel2Collapsed = true;
         btnCoverArea.Visible = false;
         btnRoute.Visible = false;
         //cbFilter.Visible = false;
         tslFilter.Visible = false;
         tsReportMenu.Visible = false;
         toolStripSeparator3.Visible = false;

         documents.Add(new DocumentInfo(dsTaskDocs, ObjType.TObjType.Task));

         DataGridViewTextBoxColumn clmn = new DataGridViewTextBoxColumn();
         clmn.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmn.DataPropertyName = "Notes";
         clmn.FillWeight = 200F;
         clmn.HeaderText = "Примечание/ответ";
         clmn.Name = "dgvOrderItemsRemark";
         dgvDetail.Columns.Add(clmn);

         clmn = new DataGridViewTextBoxColumn();
         clmn.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
         clmn.DataPropertyName = "ID";
         clmn.FillWeight = 100F;
         clmn.HeaderText = "Код";
         clmn.Name = "dgvOrderItemsRemark";
         dgvOrderItems.Columns.Insert(1, clmn);

      }

      protected override void CellFormatting(System.Windows.Forms.DataGridViewCellFormattingEventArgs e)
      {
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         string agentFilter = "\"userid\"='" + agentID + "'";
         string filter = agentFilter + String.Format(" and \"end\" >= ToDate('{0:dd/MM/yyyy}') and \"end\" <= ToDate('{1:dd/MM/yyyy}')", dateBegin, dateEnd);
         dsTask.Filter = filter;

         dsResponce.Filter = "\"id\" in (select \"id\" from \"NapoleonTask\" where " + filter + ")";

         updSets.Add(dsTask);
         updSets.Add(dsResponce);

         dsDogovors.Command = new ServerCommand(Commands.Impersonate(Commands.GET, agentID), dsDogovors.Name);
         updSets.Insert(0, dsDogovors);
      }

      protected override void AfterRefreshData()
      {
         Dictionary<string, NapoleonTaskResponse> response = new Dictionary<string, NapoleonTaskResponse>();
         foreach (NapoleonTaskResponse resp in dsResponce.Data)
            response[resp.id] = resp;

         dsTaskDocs.Clear();
         foreach(NapoleonTask task in dsTask.Data)
         {
            NapoleonTaskDoc td = new NapoleonTaskDoc(task, response.ContainsKey(task.id) ? response[task.id] : null);
            dsTaskDocs.Add(td);
         }
      }

      internal override OrdersDetail CreateOrderDetail()
      {
         return new OrdersDetailEx(documents, dsResponce);
      }

      internal override System.Windows.Forms.Control RefreshDetail(OrderDetailRepresentation odr)
      {
         if( odr.Doctype.Val == ObjType.TObjType.Task )
         {
            NapoleonTaskDoc ntd = odr.StoreObject as NapoleonTaskDoc;
            tbVisitText.Text = ntd.Taks;
            return tbVisitText;
         }
         return null;
      }

      protected override void UpdateFiltersListInComboBox()
      {
         cbFilter.SuspendLayout();
         cbFilter.Items.Clear();
         cbFilter.Items.Add("Все");

         foreach (ObjType tObjType in oDetail.FiltersAvailable)
         {
            if (tObjType.Val != ObjType.TObjType.OutRoute && tObjType.Val != ObjType.TObjType.NotVisit)
               cbFilter.Items.Add(tObjType);
         }
         cbFilter.Items.Add(new ObjType(ObjType.TObjType.NotDo));

         //cbFilter.Sorted = true;
         cbFilter.SelectedIndex = 0;
         cbFilter.ResumeLayout();
      }

      protected override void ShowCorrespondingPhoto(DateTime date, OrderDetailRepresentation odr)
      {
         lvPhoto.Clear();
         int photoCount = 0;
         List<Image> nativePicture = new List<Image>();

         NapoleonTaskDoc ntd = odr.StoreObject as NapoleonTaskDoc;
         if( ntd != null && ntd.Response != null )
         {
            NapoleonTaskResponse ntr = ntd.Response;
            if (ntr.items.Count > 0)
            {
               try
               {
                  byte[] src = ntr.items[0].id;
                  MemoryStream stream = new MemoryStream(src);
                  Image image = new Bitmap(stream);
                  //image.Tag = ntd.Created;

                  nativePicture.Add(image);
                  imPhoto.Images.Add(image);
                  stream.Close();
                  photoCount++;

                  imPhoto.Tag = nativePicture;
                  for (int i = 0; i < photoCount; i++)
                  {
                     lvPhoto.Items.Add((i + 1).ToString()).ImageIndex = i;
                  }
               }
               catch { } //TO-DO: watch in logger!!!!!

            }
         }
      }
   }

   class OrdersDetailEx : OrdersDetail
   {
      SimpleDataSet<NapoleonTaskResponse> dsResponce;
      public OrdersDetailEx(List<DocumentInfo> documents, SimpleDataSet<NapoleonTaskResponse> dsResponce) : base(documents)
      {
         this.dsResponce = dsResponce;
      }

      protected override void AddNotVisitedOrg(bool oneDay, Org org)
      {
      }

      protected override void LoadInt(FmDetailData cond, bool oneDay, bool checkRoute, string agentID, List<Org> routes)
      {
         if(cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.NotDo) : false )
         {
            Dictionary<string, bool> answered = new Dictionary<string, bool>();
            foreach (NapoleonTaskResponse r in dsResponce.Data)
               answered[r.id] = true;

            foreach (DocumentInfo di in documents)
            {
               if( di.Type == ObjType.TObjType.Task)
               {
                  CheckFiltersForDocType(di.DataSet, di.Type, filtersAvailable);
                  foreach (BaseDocument doc in di.DataSet.Data)
                  {
                     if( answered.ContainsKey(doc.id) == false)
                        Add(new OrderDetailRepresentation(doc, new ObjType(di.Type), oneDay));
                  }

                  break;
               }
            }
         } else
            base.LoadInt(cond, oneDay, checkRoute, agentID, routes);
      }
   }

   class NapoleonTaskDoc : BaseDocument
   {
      NapoleonTask task;
      NapoleonTaskResponse response;

      public NapoleonTaskDoc(NapoleonTask task, NapoleonTaskResponse response)
      {
         this.task = task;
         this.response = response;

         id = task.id;
         date = task.end;
         created = (response == null) ? DateTime.MinValue : response.created;

         if (response != null)
         {
            sended = response.sended;
            remark = response.remark;
         }

         userid = task.userid;
         agent = Agents.GetDataSet()[task.userid];

         org = new NapoleonManager.Org();
         org.id = task.id;
         org.name = task.task;

      }

      public NapoleonTaskResponse Response { get { return response; } }

      public string Taks
      {
         get
         {
            string res = "Задача: " + task.task;
            if (response != null)
            {
               res += "\t Ответ: ";
               res += response.remark;
            }
            return res;
         }
      }
   }
}
