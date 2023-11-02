using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Windows.Forms;
using System.Drawing;
using System.Reflection;
using System.IO;

namespace GRSoft.NapoleonManager
{
   interface DataObjectViewer
   {
      void SetData(GRSoft.Network.DataObject dataObject);
   }

   public class FmDetail : FmDetailBase
   {
      protected DataSet<int, ScriptDoc> dsScriptDoc;
      protected DataSet<int, ScriptDef> dsScriptDef;
      public DataSet<int, Contract> dsContract;
      //public ToolStripMenuItem miModeScript;
      //public ToolStripMenuItem miModeDoc;
      //protected ToolStripSplitButton sbMode;

      protected TabControl scriptDetail;

      protected class DocView
      {
         public String docType;
         public String title;
         public Type viewer; // implements DataObjectViewer

         public DocView(String type, String title, Type viewer)
         {
            this.docType = type;
            this.title = title;
            this.viewer = viewer;
         }

         public TabPage MakePage()
         {
            if (!typeof(DataObjectViewer).IsAssignableFrom(viewer))
               return null;

            Control c = MakeControl();

            if (c == null)
               return null;

            TabPage pg = new TabPage(docType);
            pg.Text = title;
            pg.Controls.Add(c);
            c.Dock = DockStyle.Fill;

            return pg;
         }

         public Control MakeControl()
         {
            Control result = null;

            ConstructorInfo ctor = viewer.GetConstructor(System.Type.EmptyTypes);
            if (ctor != null)
               result = ctor.Invoke(null) as Control;

            return result;
         }
      }

      protected DocView[] docViews = 
      { 
         new DocView(ScriptDoc.OBJECT_NAME, "Визит", typeof(ScriptOverview)),
#if Prodo || Halygov
         new DocView(Order.OBJECT_NAME, "Заявка", typeof(OrderOverviewEx)),
#else
         new DocView(Order.OBJECT_NAME, "Заявка", typeof(OrderOverview)),
#endif
         new DocView(OrgRemnants.OBJECT_NAME, "Остатки", FormEntries.GetFormType(typeof(RemnantsOverview))),
         new DocView(Returns.OBJECT_NAME, "Возвраты", typeof(ReturnOverview)),
         new DocView(Sales.OBJECT_NAME, "Продажи", typeof(OrderOverview)),
         new DocView(Answer.OBJECT_NAME, "Вопросы", typeof(AnswerOverview)),
         new DocView(Contract.OBJECT_NAME, "Контракт", typeof(ContractOverview))
      };

      public FmDetail(FmDetailData data)
         : base(data)
      {
         dsContract = (DataSet<int, Contract>)DataModule.Get(Contract.OBJECT_NAME) ?? new DataSet<int, Contract>(Contract.OBJECT_NAME);
         dsScriptDoc = DataModule.Get(ScriptDoc.OBJECT_NAME) == null ? new DataSet<int, ScriptDoc>(ScriptDoc.OBJECT_NAME) :
            (DataSet<int, ScriptDoc>)DataModule.Get(ScriptDoc.OBJECT_NAME);
         dsScriptDef = DataModule.Get(ScriptDef.OBJECT_NAME) == null ? new DataSet<int, ScriptDef>(ScriptDef.OBJECT_NAME) :
            (DataSet<int, ScriptDef>)DataModule.Get(ScriptDef.OBJECT_NAME);

         scriptDetail = new TabControl();
         scriptDetail.MouseWheel += delegate(object sender, MouseEventArgs e)
         {
            int row = dgvDetail.FirstDisplayedScrollingRowIndex;
            row = e.Delta > 0 ? --row : ++row;

            if (row >= 0 && row < dgvDetail.Rows.Count - 1)
               dgvDetail.FirstDisplayedScrollingRowIndex = row;
         };

         detailPanel.Controls.Add(scriptDetail);
         scriptDetail.Dock = DockStyle.Fill;
         scriptDetail.Visible = true;

         documents.Add(new DocumentInfo(dsContract, ObjType.TObjType.Contract));

         //miModeScript = new System.Windows.Forms.ToolStripMenuItem();
         //miModeDoc = new System.Windows.Forms.ToolStripMenuItem();
          
         //miModeScript.Checked = true;
         //miModeScript.CheckState = System.Windows.Forms.CheckState.Checked;
         //miModeScript.Image = NapoleonMonitor.Properties.Resources.script_doc;
         //miModeScript.Name = "miModeScript";
         //miModeScript.Size = new System.Drawing.Size(148, 22);
         //miModeScript.Text = "Сценарии";
         //miModeScript.Click += new System.EventHandler(this.miModeScript_Click);

         //miModeDoc.Image = NapoleonMonitor.Properties.Resources.order_doc;
         //miModeDoc.Name = "miModeDoc";
         //miModeDoc.Size = new System.Drawing.Size(148, 22);
         //miModeDoc.Text = "Документы";
         //miModeDoc.Click += new System.EventHandler(this.miModeDoc_Click);

         //sbMode = new ToolStripSplitButton();
         //sbMode.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         //sbMode.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
         //   miModeScript,
         //   miModeDoc});
         //sbMode.Image = NapoleonMonitor.Properties.Resources.script_doc;
         //sbMode.ImageTransparentColor = System.Drawing.Color.Magenta;
         //sbMode.Name = "sbMode";
         //sbMode.Size = new System.Drawing.Size(32, 22);
         //sbMode.Text = String.Empty;
         //sbMode.ToolTipText = "Сценарии";
         //sbMode.ButtonClick += new System.EventHandler(this.sbMode_Click);
         //sbMode.Visible = false;
//#if !BTL
//         ToolStripSeparator toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
//         toolStripSeparator1.Name = "toolStripSeparator1";
//         toolStripSeparator1.Size = new System.Drawing.Size(6, 25);

//         toolStrip1.Items.Add(toolStripSeparator1);
//         toolStrip1.Items.Add(sbMode);
//#endif

//#if SCRIPT_DOC
//         sbMode.Visible = true;
//         scriptDetail.BringToFront();
//#endif

         SetScriptMode(false);
      }

      public void SetScriptMode(bool scriptMode)
      {
         AdjustRangeButton(scriptMode, (scriptMode) ? "Сценарий" : "Документы");
      }

      //Условие выборки "за сегодня"
      private void miModeScript_Click(object sender, EventArgs e)
      {
         SetScriptMode(true);
         ReloadData();

         UpdateDetailTable(dgvDetail.CurrentRow);
      }

      //Условие выборки "за период"
      private void miModeDoc_Click(object sender, EventArgs e)
      {
         SetScriptMode(false);
         ReloadData();

         UpdateDetailTable(dgvDetail.CurrentRow);
      }

      //Настройка кнопок для выбора периода 
      private void AdjustRangeButton(bool isScript, string toolTipText)
      {
         //sbMode.Image = isScript ? miModeScript.Image : miModeDoc.Image;
         //miModeScript.Checked = isScript;
         //miModeDoc.Checked = !isScript;
         //sbMode.ToolTipText = toolTipText;
      }

      ////Переключение условий выборки по щелчку на кнопку
      //private void sbMode_Click(object sender, EventArgs e)
      //{
      //   if (miModeScript.Checked)
      //   {
      //      miModeDoc_Click(sender, e);
      //   }
      //   else
      //   {
      //      miModeScript_Click(sender, e);
      //   }
      //}

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         dsContract.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsScriptDoc.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsScriptDef.Filter = "\"userid\" = '' or \"userid\" is null";
         updSets.Add(dsScriptDoc);
         updSets.Add(dsScriptDef);
         updSets.Add(dsContract);
      }

      protected override void AfterRefreshData()
      {
         List<int> removed = new List<int>();
         Manager m = (CurrentUser.user) as Manager;
         foreach (KeyValuePair<int, ScriptDef> sd in dsScriptDef)
            if (m.HaveContract(sd.Value.cdefid) == false)
               removed.Add(sd.Key);

         foreach (int val in removed)
            dsScriptDef.Remove(val);

         removed.Clear();
         foreach (KeyValuePair<int, ScriptDoc> sd in dsScriptDoc)
            if (dsScriptDef.ContainsKey(sd.Value.scriptId) == false)
               removed.Add(sd.Key);

         foreach (int val in removed)
            dsScriptDoc.Remove(val);
      }

      protected override Order GetOrder(System.Windows.Forms.DataGridViewRow curRow)
      {
         if (curRow != null)
         {
            ScriptDoc ret = (curRow.DataBoundItem as OrderDetailRepresentation).StoreObject as ScriptDoc;
            if (ret != null)
               return ret.GetDocument(Order.OBJECT_NAME) as Order;
         }

         return base.GetOrder(curRow);
      }

      protected override Incass GetIncass(System.Windows.Forms.DataGridViewRow curRow)
      {
         if (curRow != null)
         {
            ScriptDoc ret = (curRow.DataBoundItem as OrderDetailRepresentation).StoreObject as ScriptDoc;
            if (ret != null)
               return ret.GetDocument(Incass.OBJECT_NAME) as Incass;
         }

         return base.GetIncass(curRow);
      }

      internal override OrdersDetail CreateOrderDetail() { return new ScriptDetail(documents); }

      protected override void GetDocData(out DateTime created, out Org docOrg, GRSoft.Network.DataObject dataObject)
      {
         ScriptDoc sd = dataObject as ScriptDoc;
         if (sd != null)
         {
            created = sd.created;
            docOrg = sd.org;
         } else
            base.GetDocData(out created, out docOrg, dataObject);
      }

      protected virtual void UpdateDetail(OrderDetailRepresentation odr) { }

      protected override void UpdateDetailTable(DataGridViewRow curRow)
      {
         if (!IsScriptMode)
         {
            scriptDetail.Visible = false;
            base.UpdateDetailTable(curRow);
         }
         else
         {
            lvPhoto.Items.Clear();
            imPhoto.Images.Clear();

            if (curRow != null)
            {
               dgvOrderItems.Visible = false;
               dgvRemnantsItems.Visible = false;
               tbVisitText.Visible = false;
               dgvOrderItems.Visible = false;

               OrderDetailRepresentation odr = curRow.DataBoundItem as OrderDetailRepresentation;
               ScriptDoc sd = odr.StoreObject as ScriptDoc;

               if (sd != null)
               {
                  scriptDetail.Visible = true;
                  SetScriptInfo(sd);

                  lbNotes.Text = "";
                  if (dsScriptDef.ContainsKey(sd.scriptId))
                  {
                     lbNotes.Text = dsScriptDef[sd.scriptId].name;
                     lbNotes.Visible = true;
                  }
               }
               else
               {
                  scriptDetail.Visible = false;
                  tbVisitText.Visible = true;
                  tbVisitText.Text = "Не посетил";
                  lbNotes.Text = "";

                  UpdateDetail(odr);
               }

               SetLabelAddressText(curRow);
            }
            else
            {
               dgvOrderItems.DataSource = new List<OrgRemnantsItem>();
               dgvRemnantsItems.DataSource = new List<OrgRemnantsItem>();
               lblAdress.Text = string.Empty;
               dgvRemnantsItems.Height = dgvRemnantsItems.Height + lbNotes.Height;
               return;
            }
         }
      }

      protected virtual DocView GetDocView(String docType)
      {
         foreach (DocView dv in docViews)
            if (dv.docType.Equals(docType))
               return dv;

         return null;
      }

      protected override bool IsDocCompleted(DateTime date, GRSoft.Network.DataObject dataObject)
      {
         ScriptDoc sd = dataObject as ScriptDoc;
         if (sd != null)
         {
            foreach (ScriptDocItem i in sd.items)
               if (!i.Inited)
                  return false;
            //ScriptDef def = null;
            //if (dsScriptDef.ContainsKey(sd.scriptId))
            //   def = dsScriptDef[sd.scriptId];

            //if (def != null)
            //   return def.items.Count == sd.items.Count;
            return true;
         }
         return base.IsDocCompleted(date, dataObject);
      }

      private void SetScriptInfo(ScriptDoc sd)
      {
         scriptDetail.SuspendLayout();
         scriptDetail.TabPages.Clear();

         if (sd != null)
         {
            ScriptDef def = null;

            if (dsScriptDef.ContainsKey(sd.scriptId))
               def = dsScriptDef[sd.scriptId];

            if (def != null)
            {
               DocView odv = GetDocView(ScriptDoc.OBJECT_NAME);
               if (odv != null)
               {
                  TabPage tp = odv.MakePage();
                  scriptDetail.TabPages.Add(tp);
                  ((DataObjectViewer)tp.Controls[0]).SetData(sd);
               }

               int index = 0;
               List<Image> nativePicture = new List<Image>();
               foreach (ScriptDocItem i in sd.items)
               {
                  if (!i.Inited)
                  {
                     index++;
                     continue;
                  }

                  if (IsVisitItem(i))
                  {
                     Visit v = i.Document as Visit;
                     if (v != null)
                     {
                        int count = 0;
                        foreach (Visit.VisitItem vi in v.items)
                        {
                           if (vi.id != null)
                           {
                              MemoryStream stream = new MemoryStream(vi.id);
                              Image image = new Bitmap(stream);
                              image.Tag = new VisitTag(v, vi);

                              nativePicture.Add(image);
                              imPhoto.Images.Add(image);

                              count++;
                              String tag = count.ToString();
                              if (def != null && def.items.Count > index)
                              {
                                 ScriptDefItem sdi = def.items[index];
                                 if (sdi.name.Length > 0)
                                    tag = sdi.name + " " + tag;
                              }
#if SNAPSHOT_RATING
                        if (vi.rating != 0)
                           tag += "\nОценка: " + vi.rating.ToString();
#endif
                              ListViewItem lvi = lvPhoto.Items.Add(tag);
                              lvi.ImageIndex = nativePicture.Count - 1;
                              lvi.Tag = new VisitTag(v, vi);
                           }
                        }
                     }
                  }
                  else
                  {
                     odv = GetDocView(i.type);
                     if (odv != null)
                     {
                        TabPage tp = odv.MakePage();
                        scriptDetail.TabPages.Add(tp);

                        if (def != null && def.items.Count > index)
                        {
                           ScriptDefItem sdi = def.items[index];
                           if (sdi.name.Length > 0)
                              tp.Text = sdi.name;
                        }

                        GRSoft.Network.DataObject doc = i.Document;
                        if (doc != null)
                           ((DataObjectViewer)tp.Controls[0]).SetData(doc);
                     }
                  }
                  index++;
               }

               PostSetScriptInfo(sd);
               imPhoto.Tag = nativePicture;
            }
         }
         scriptDetail.ResumeLayout();
         scriptDetail.BringToFront();
      }

      protected virtual bool IsVisitItem(ScriptDocItem i)
      {
         return i.type == Visit.OBJECT_NAME;
      }

      protected virtual void PostSetScriptInfo(ScriptDoc sd) { }

      public bool IsScriptMode { get { return true; } } // miModeScript.Checked; } }
   }

   class ScriptDetail : OrdersDetail
   {
      public ScriptDetail() {}
      public ScriptDetail(List<DocumentInfo> documents) : base(documents) {}

      protected virtual bool isEmptyScript(List<ScriptDocItem> items)
      {
         bool result = true;

         if (items != null)
            foreach(ScriptDocItem s in items)
               if(s.Document != null)
               {
                  result = false;
                  break;
               }

         return result;
      }

      protected override void LoadInt(FmDetailData cond, bool oneDay, bool checkRoute, string agentID, List<Org> routes)
      {
         //if ( !((FmDetail)cond.fmDetail).IsScriptMode )
         //   base.LoadInt(cond, oneDay, checkRoute, agentID, routes);
         //else
         {
            IDataSet cdata = DataModule.Get(ScriptDoc.OBJECT_NAME);
            if (cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.Script) : true && cdata != null)
            {
               foreach (ScriptDoc doc in cdata.Data)
               {
                  if (checkRoute 
                        && FmDetailBase.IsCreatedBySelectedAgentRoute(doc.org, agentID, doc.created) || 
                           isEmptyScript(doc.items)
                     )
                     continue;

                  double osum = 0;
                  int qty = 0;
                  List<GRSoft.Network.DataObject> orders = doc.GetDocumentsOfType(Order.OBJECT_NAME);

                  foreach (Order o in orders)
                  {
                     if (o != null)
                     {
                        docCount++;
                        osum += o.DSum;
                        qty += o.Qty;
                     }
                  }
                  
                  sum += osum;
                  Add(new OrderDetailRepresentation(doc.Created, new ObjType(ObjType.TObjType.Script),
                     doc.Created, doc.Sended, doc.org, osum, 0, qty, doc, oneDay, ""));
               }
            }

            cdata = DataModule.Get(Visit.OBJECT_NAME);

            if (cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.OtVisit) : true && cdata != null)
            {
               int key = 0;
               foreach (Visit doc in cdata.Data)
               {
                  if (typeof(PotenzialOrg) == doc.org.GetType())
                  {
                     ScriptDoc sc = new ScriptDoc();
                     sc.org = doc.org;
                     sc.id = doc.org.id;
                     sc.scriptId = key;
                     sc.agent = doc.agent;
                     sc.created = doc.created;
                     sc.sended = doc.sended;

                     ScriptDocItem item = new ScriptDocItem();
                     item.date = doc.created;
                     item.type = Visit.OBJECT_NAME;
                     item.state = ScriptDocItem.DOC_INITED;

                     sc.items = new List<ScriptDocItem>();
                     sc.items.Add(item);

                     Add(new OrderDetailRepresentation(sc.created, new ObjType(ObjType.TObjType.Script),
                        sc.created, sc.sended, sc.org, sc.sum, 0, 0, sc, oneDay, ""));

                     key++;
                  }
               }
            }

            filtersAvailable.Add(new ObjType(ObjType.TObjType.Script));
         }
      }
   }
}
