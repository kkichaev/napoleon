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

   [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public class FmDetail : FmDetailBase
   {
      protected DataSet<int, ScriptDoc> dsScriptDoc;
      protected DataSet<int, ScriptDef> dsScriptDef;
      public ToolStripMenuItem miModeScript;
      public ToolStripMenuItem miModeDoc;
      protected ToolStripSplitButton sbMode;
      protected ToolStripSeparator tss;

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

         public virtual Control MakeControl()
         {
            Control result = null;

            ConstructorInfo ctor = viewer.GetConstructor(System.Type.EmptyTypes);
            if (ctor != null)
            {
                result = ctor.Invoke(null) as Control;
                if (result != null)
                    result.Name = viewer.Name;
            }

            return result;
         }
      }

      protected DocView[] docViews = 
      { 
         new DocView(ScriptDoc.OBJECT_NAME, "Визит", FormEntries.GetFormType(typeof(ScriptOverview))),
#if Prodo || Halygov
         new DocView(Order.OBJECT_NAME, "Заявка", typeof(OrderOverviewEx)),
#else
         new DocView(Order.OBJECT_NAME, "Заявка", typeof(OrderOverview)),
#endif
         new DocView(OrgRemnants.OBJECT_NAME, "Остатки", FormEntries.GetFormType(typeof(RemnantsOverview))),
         new DocView(Returns.OBJECT_NAME, "Возвраты", typeof(ReturnOverview)),
         new DocView(Sales.OBJECT_NAME, "Продажи", typeof(OrderOverview)),
         new DocView(Answer.OBJECT_NAME, "Вопросы", typeof(AnswerOverview)),
         new DocView(MoneyProxy.OBJECT_NAME, "Доверенность", typeof(MoneyProxyDetail)),
      };

      public FmDetail(FmDetailData data)
         : base(data)
      {
         dgvDetailScriptName.Visible = false;

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

         miModeScript = new System.Windows.Forms.ToolStripMenuItem();
         miModeDoc = new System.Windows.Forms.ToolStripMenuItem();
          
         miModeScript.Checked = true;
         miModeScript.CheckState = System.Windows.Forms.CheckState.Checked;
         miModeScript.Image = NapoleonManager.Properties.Resources.script_doc;
         miModeScript.Name = "miModeScript";
         miModeScript.Size = new System.Drawing.Size(148, 22);
         miModeScript.Text = "Сценарии";
         miModeScript.Click += new System.EventHandler(this.miModeScript_Click);

         miModeDoc.Image = NapoleonManager.Properties.Resources.order_doc;
         miModeDoc.Name = "miModeDoc";
         miModeDoc.Size = new System.Drawing.Size(148, 22);
         miModeDoc.Text = "Документы";
         miModeDoc.Click += new System.EventHandler(this.miModeDoc_Click);

         sbMode = new ToolStripSplitButton();
         sbMode.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
         sbMode.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            miModeScript,
            miModeDoc});
         sbMode.Image = NapoleonManager.Properties.Resources.script_doc;
         sbMode.ImageTransparentColor = System.Drawing.Color.Magenta;
         sbMode.Name = "sbMode";
         sbMode.Size = new System.Drawing.Size(32, 22);
         sbMode.Text = String.Empty;
         sbMode.ToolTipText = "Сценарии";
         sbMode.ButtonClick += new System.EventHandler(this.sbMode_Click);
         sbMode.Visible = false;
#if !BTL
         tss = new System.Windows.Forms.ToolStripSeparator();
         tss.Name = "toolStripSeparator1";
         tss.Size = new System.Drawing.Size(6, 25);

         toolStrip1.Items.Add(tss);
         toolStrip1.Items.Add(sbMode);
#endif

#if SCRIPT_DOC
         sbMode.Visible = true;
         scriptDetail.BringToFront();

         cbAgents.SelectionChangeCommitted += cbAgents_SelectionChangeCommitted;
#endif

         SetScriptMode(false);
      }

      protected virtual void cbAgents_SelectionChangeCommitted(object sender, EventArgs e)
      {
         Agent a = ((ComboBox)sender).SelectedItem as Agent;

         if (a != null)
         {
            CommonConfig cc = ConfigUtils.GetConfig(dsConfig, ConfigKeyItems.ALLOW_SCRIPTING, a.id);
            SetScriptMode((cc == null) ? false : (int.Parse(cc.value) > 0));
         }
      }

      public virtual void SetScriptMode(bool scriptMode)
      {
         AdjustRangeButton(scriptMode, (scriptMode) ? "Сценарий" : "Документы");
      }

      //Условие выборки "за сегодня"
      protected virtual void miModeScript_Click(object sender, EventArgs e)
      {
         SetScriptMode(true);
         ReloadData();

         UpdateDetailTable(dgvDetail.CurrentRow);
      }

      //Условие выборки "за период"
      protected virtual void miModeDoc_Click(object sender, EventArgs e)
      {
         SetScriptMode(false);
         ReloadData();

         UpdateDetailTable(dgvDetail.CurrentRow);
      }

      //Настройка кнопок для выбора периода 
      private void AdjustRangeButton(bool isScript, string toolTipText)
      {
         sbMode.Image = isScript ? miModeScript.Image : miModeDoc.Image;
         miModeScript.Checked = isScript;
         miModeDoc.Checked = !isScript;
         sbMode.ToolTipText = toolTipText;
      }

      //Переключение условий выборки по щелчку на кнопку
      private void sbMode_Click(object sender, EventArgs e)
      {
         if (miModeScript.Checked)
         {
            miModeDoc_Click(sender, e);
         }
         else
         {
            miModeScript_Click(sender, e);
         }
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         dsScriptDoc.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
#if Discount
         dsScriptDef.Filter = String.Format("\"userid\" in ('{0}')", agentID);
#else
         dsScriptDef.Filter = "\"userid\" = '' or \"userid\" is null";
#endif
         updSets.Add(dsScriptDoc);
         updSets.Add(dsScriptDef);
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
            StringBuilder sb = new StringBuilder();
            StartPhotoHTML(sb);
            UpdateWebHtml(sb);

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
#if KMZavod
            return true;
#endif
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

      protected virtual void SetScriptInfo(ScriptDoc sd)
      {
         scriptDetail.SuspendLayout();
         scriptDetail.TabPages.Clear();

         StringBuilder htmlBuilder = new StringBuilder();
         if (sd != null)
         {
            ScriptDef def = null;
            
            if(dsScriptDef.ContainsKey(sd.scriptId) )
               def = dsScriptDef[sd.scriptId];

            DocView odv = GetDocView(ScriptDoc.OBJECT_NAME);
            if (odv != null)
            {
               TabPage tp = odv.MakePage();
               scriptDetail.TabPages.Add(tp);
               ((DataObjectViewer)tp.Controls[0]).SetData(sd);
            }

            int index = 0;
            List<Image> nativePicture = new List<Image>();
#if HTTP_SERVER
            bool addBr = false;
            StartPhotoHTML(htmlBuilder);
#endif
            
            // Postgre SQL mix items!!!
            if (sd.items.Count > 0)
            {
               if(sd.items[0].pos != 0)
                  sd.items.Sort();
            }

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
#if HTTP_SERVER
                     string docDate = v.created.ToString("dd.MM.yy HH:mm");
                     int count = 0;

#if VISIT_ITEM_DATE
                     v.items.Sort((x, y) => x.date.CompareTo(y.date));
#endif
                     foreach (Visit.VisitItem vi in v.items)
                     {
                        count++;
                        String tag = count.ToString();
                        if (def != null && def.items.Count > index)
                        {
                           ScriptDefItem sdi = def.items[index];
                           if (sdi.curType.Equals("Visit") && sdi.name.Length > 0)
                              tag = sdi.name + " " + tag;
                        }
                        DateTime photoCr = DateTime.MinValue;
#if VISIT_ITEM_DATE
                        photoCr = vi.date;
#endif
                        if (vi.date > v.created)
                        {
                           docDate = vi.date.ToString("dd.MM.yy HH:mm");
                        }
                        if (AddPhotoToHtml(htmlBuilder, tag, vi.name, vi.smallName, vi.smallSize, docDate, photoCr))
                           addBr = true;
                     }
                     if (addBr)
                        htmlBuilder.Append("<br/>");
#else
                     int count = 0;
                     foreach (Visit.VisitItem vi in v.items)
                     {
                        if (vi.id != null)
                        {
                           try
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
                           catch (Exception)
                           {
                           }
                        }
                     }
#endif
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
                     if( doc != null )
                        SetControlData((DataObjectViewer)tp.Controls[0], doc);
                  }

                  if (i.type == Answer.OBJECT_NAME)
                  {
                     Answer a = i.Document as Answer;
                     if (a != null)
                        AddAnswerPhotos(htmlBuilder, a);
                  }
               }
               index++;
            }

            AddObjectPhoto(htmlBuilder, sd);
            PostSetScriptInfo(sd);
            imPhoto.Tag = nativePicture;
         }
         scriptDetail.ResumeLayout();
         scriptDetail.BringToFront();

#if HTTP_SERVER
         UpdateWebHtml(htmlBuilder);
#endif
      }

      internal virtual void SetControlData(DataObjectViewer control, GRSoft.Network.DataObject doc)
      {
         control.SetData(doc);
      }

      protected virtual bool IsVisitItem(ScriptDocItem i)
      {
         return i.type == Visit.OBJECT_NAME;
      }

      protected virtual void PostSetScriptInfo(ScriptDoc sd) { }

      public virtual bool IsScriptMode { get { return miModeScript.Checked; } }

      internal override Control RefreshDetail(OrderDetailRepresentation odr)
      {
         Control res = base.RefreshDetail(odr);

         if (res == null)
         {
            DocView dv = GetDocView(Enum.GetName(typeof(ObjType.TObjType), odr.Doctype.Val));
            if (dv != null)
            {
               res = FindDetailControl(dv);
               if (res == null)
               {
                  res = dv.MakeControl();
                  detailPanel.Controls.Add(res);
                  res.Dock = DockStyle.Fill;
               }

               SetDocViewData(odr, res);
            }
         }

         return res;
      }

      protected virtual void SetDocViewData(OrderDetailRepresentation odr, Control res)
      {
         DataObjectViewer dov = res as DataObjectViewer;

         if (dov != null)
         {
            dov.SetData(odr.StoreObject);
         }
      }

      private Control FindDetailControl(DocView dv)
      {
         Control result = null;

         foreach (Control cc in scBottom.Panel1.Controls)
            if (cc.Name.Equals(dv.viewer.Name))
            {
               result = cc;
               break;
            }

         return result;
      }

   }

   public class ScriptDetail : OrdersDetail
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
         if (!((FmDetail)cond.fmDetail).IsScriptMode)
         {
            ((FmDetail)cond.fmDetail).dgvDetailScriptName.Visible = false;
            base.LoadInt(cond, oneDay, checkRoute, agentID, routes);
         }
         else
         {
            usedDocs.Add(ScriptDoc.OBJECT_NAME);
            ((FmDetail)cond.fmDetail).dgvDetailScriptName.Visible = true;
            IDataSet cdata = DataModule.Get(ScriptDoc.OBJECT_NAME);
            if (cond.OrderType != null ? cond.OrderType.Equals(ObjType.TObjType.Script) : true && cdata != null)
            {
               DataSet<int, ScriptDef> defs = DataModule.Get(ScriptDef.OBJECT_NAME) as DataSet<int, ScriptDef>;

               foreach (ScriptDoc doc in cdata.Data)
               {
                  if (checkRoute
                        && FmDetailBase.IsCreatedBySelectedAgentRoute(doc.org, agentID, doc.created) ||
                           isEmptyScript(doc.items)
                     )
                     continue;

                  if (!LoadIntDocument(cond, doc))
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

                  sum += doc.Sum();
                  qty += doc.Qty;
                  docCount += doc.OrderCnt();
                  OrderDetailRepresentation odr = new OrderDetailRepresentation(doc, new ObjType(ObjType.TObjType.Script), oneDay);

                  if (defs != null && defs.ContainsKey(doc.scriptId))
                     odr.ScriptName = defs[doc.scriptId].Name;

                  Add(odr);
               }
            }

            //LoadPotenzialOrgVisit(cond, oneDay);

            filtersAvailable.Add(new ObjType(ObjType.TObjType.Script));
         }
      }

      protected virtual void LoadPotenzialOrgVisit(FmDetailData cond, bool oneDay)
      {
#if HTTP_SERVER
         string objName = Visit.OBJECT_NAME_HTTP;
#else
         string objName = Visit.OBJECT_NAME_LITE;
#endif
         IDataSet cdata = DataModule.Get(objName);

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
      }
   }
}
