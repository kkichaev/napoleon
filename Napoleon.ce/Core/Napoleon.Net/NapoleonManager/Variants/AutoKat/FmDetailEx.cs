using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Security.Policy;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
   [System.Runtime.InteropServices.ComVisibleAttribute(true)]
   public class FmDetailEx : FmDetail
   {
      public DataSet<int, Purchase> dsPurchase;
      public DataSet<int, Selling> dsSelling;
      public DataSet<int, PicStore> dsPicStore;
      ToolStripItem itDeleteVisit;
      ToolStripItem itDeleteOrg;
      ToolStripItem itDeleteDoc;

      public FmDetailEx(FmDetailData data)
         : base(data)
      {

         dsPurchase = (DataSet<int, Purchase>)DataModule.Get(Purchase.OBJECT_NAME) ?? new DataSet<int, Purchase>(Purchase.OBJECT_NAME);
         dsSelling = (DataSet<int, Selling>)DataModule.Get(Selling.OBJECT_NAME) ?? new DataSet<int, Selling>(Selling.OBJECT_NAME);
         dsPicStore= (DataSet<int, PicStore>)DataModule.Get(PicStore.OBJECT_NAME_SRC) ?? new DataSet<int, PicStore>(PicStore.OBJECT_NAME_SRC);


         btnCoverArea.Visible = false;
         tslFilter.Margin = new System.Windows.Forms.Padding(0, 1, 250, 2);
         tsReportMenu.Visible = false;
         btnCoverArea.Visible = false;
         tss.Visible = false;

         miModeScript.Checked = true;
         miMakeDup.Text = "Экспорт в ERP";

         List<DocView> views = new List<DocView>(docViews);
         views.Add(new DocView(Purchase.OBJECT_NAME, "Закуп", typeof(PurchaseView)));
         views.Add(new DocView(Selling.OBJECT_NAME, "Продажа сопутствующих товаров", typeof(OrderOverview)));
         
         docViews = views.ToArray();

         dgvDetailColumnSum.HeaderText = "Сумма закупа";
         tslFilter.Visible = false;
         cbFilter.Visible = false;

         documents.Add(new DocumentInfo(dsPurchase, ObjType.TObjType.PurchaseDoc));
         documents.Add(new DocumentInfo(dsSelling, ObjType.TObjType.Sales));

         itDeleteVisit = new ToolStripMenuItem();
         itDeleteVisit.Text = "Удалить Визит";
         itDeleteVisit.Click += ItDeleteVisit_Click;

         itDeleteOrg = new ToolStripMenuItem();
         itDeleteOrg.Text = "Удалить клиента";
         itDeleteOrg.Click += ItDeleteOrg_Click;

         itDeleteDoc = new ToolStripMenuItem();
         itDeleteDoc.Text = "Удалить документ";
         itDeleteDoc.Click += ItDeleteDoc_Click;


         if (CanRemove())
         {
            cmDgvDetail.Items.Add(itDeleteVisit);
            cmDgvDetail.Items.Add(itDeleteOrg);
            cmDgvDetail.Items.Add(itDeleteDoc);
         }
      }

      private void ItDeleteDoc_Click(object sender, EventArgs e)
      {
         OrderDetailRepresentation odr = dgvDetail.CurrentRow.DataBoundItem as OrderDetailRepresentation;
         if (odr == null && odr.dataObject == null)
            return;

         List<IDataSet> remove = new List<IDataSet>();

         if (odr.dataObject is Selling)
         {
            DataSet<int, Selling> ds = new DataSet<int, Selling>(Selling.OBJECT_NAME, false);
            ds.Add(0, odr.dataObject);
            remove.Add(ds);
         }
         else if (odr.dataObject is Purchase)
         {
            DataSet<int, Purchase> ds = new DataSet<int, Purchase>(Purchase.OBJECT_NAME, false);
            ds.Add(0, odr.dataObject);
            remove.Add(ds);
         }

         Config cfg = Config.GetConfig();

         if (DataModule.UpdateDataSet(null, remove, null, cfg.GetConnection()))
            DialogUtil.SavedGood(this);
         else
            DialogUtil.UpdateErrMsg(this);

         btnRefresh.PerformClick();
      }

      private void ItDeleteOrg_Click(object sender, EventArgs e)
      {
         if (MessageBox.Show(this, "Вы действительно хотите удалить клиента и все его документы?",
            "Внимание", MessageBoxButtons.OKCancel) == DialogResult.OK)
         {
            OrderDetailRepresentation odr = dgvDetail.CurrentRow.DataBoundItem as OrderDetailRepresentation;
            if (odr == null && odr.dataObject == null)
               return;

            string agentid = GetSelectedIdAgent();

            if (agentid == null) return;

            SimpleDataSet<ScriptDoc> scripts = new SimpleDataSet<ScriptDoc>(ScriptDoc.OBJECT_NAME);
            scripts.Filter = string.Format("\"created\">=ToDate('{0:dd/MM/yyyy}' and \"userid\"='{1}')",
               DateTime.Now.AddMonths(-3), agentid);

            Config cfg = Config.GetConfig();

            DataModule.RefreshGiveSets(cfg.GetConnection(), scripts, null).Join();

            SimpleDataSet<NeedRemove> remOldNR = new SimpleDataSet<NeedRemove>(NeedRemove.OBJECT_NAME, false);
            remOldNR.Filter = string.Format("\"created\"<=ToDate('{0:dd/MM/yyyy}')", DateTime.Now.AddDays(-3));
            DataModule.RemoveDataSet(remOldNR, cfg.GetConnection());

            ScriptDoc scr = FindScript(odr);

            if (scr == null) return;

            NeedRemove nr = new NeedRemove();
            nr.id = string.Format("{0}\t{1}\t{2}", odr.ID, scr.fio, scr.phone);
            nr.created = DateTime.Now;
            nr.userid = GetSelectedIdAgent();

            SimpleDataSet<Visit> rmv = new SimpleDataSet<Visit>(Visit.OBJECT_NAME, false);
            SimpleDataSet<Answer> rma = new SimpleDataSet<Answer>(Answer.OBJECT_NAME, false);
            SimpleDataSet<Purchase> rmp = new SimpleDataSet<Purchase>(Purchase.OBJECT_NAME, false);
            SimpleDataSet<Selling> rms = new SimpleDataSet<Selling>(Selling.OBJECT_NAME, false);
            SimpleDataSet<ScriptDoc> rmscr = new SimpleDataSet<ScriptDoc>(ScriptDoc.OBJECT_NAME, false);

            foreach (ScriptDoc s in scripts.Values)
            {
               if (s.id.Equals(scr.id) && s.fio.Equals(scr.fio) && s.phone.Equals(scr.phone))
               {
                  rmscr.Add(s);

                  if (!s.visitDoc.Equals(DateTime.MinValue))
                  {
                     Visit v = new Visit();
                     v.userid = s.userid;
                     v.created = s.visitDoc;
                     rmv.Add(v);
                  }

                  foreach (ScriptDocItem i in s.items)
                  {
                     if (i.type.Equals(Answer.OBJECT_NAME))
                     {
                        Answer a = new Answer();
                        a.created = i.date;
                        a.userid = s.userid;
                        rma.Add(a);
                     }
                     else if (i.type.Equals(Purchase.OBJECT_NAME))
                     {
                        Purchase p = new Purchase();
                        p.created = i.date;
                        p.userid = s.userid;
                        rmp.Add(p);
                     }
                     else if (i.type.Equals(Selling.OBJECT_NAME))
                     {
                        Selling h = new Selling();
                        h.created = i.date;
                        h.userid = s.userid;
                        rms.Add(h);
                     }
                  }
               }
            }

            if (rmscr.Count == 0) { return; }

            List<IDataSet> write = new List<IDataSet>();
            List<IDataSet> remove = new List<IDataSet>();

            SimpleDataSet<NeedRemove> ds = new SimpleDataSet<NeedRemove>(NeedRemove.OBJECT_NAME, false);
            ds.Add(nr);
            write.Add(ds);
            remove.Add(rmscr);
            remove.Add(rma);
            remove.Add(rmv);
            remove.Add(rms);
            remove.Add(rmp);

            if (DataModule.UpdateDataSet(write, remove, null, cfg.GetConnection()))
               DialogUtil.SavedGood(this);
            else
               DialogUtil.UpdateErrMsg(this);

            btnRefresh.PerformClick();
         }
      }

      private ScriptDoc FindScript(OrderDetailRepresentation odr)
      {
         BaseDocument bd = odr.dataObject as BaseDocument;

         if (bd == null) return null;

         foreach (ScriptDoc sd in dsScriptDoc.Values)
         {
            if (odr.Doctype.Val == ObjType.TObjType.OtVisit)
            {
               if (sd.visitDoc.Equals(bd.created))
               {
                  return sd;
               }
            }
            else
               foreach (ScriptDocItem i in sd.items)
               {
                  if (i.date.Equals(bd.created))
                  {
                     return sd;
                  }
               }
         }

         return null;
      }

      private void ItDeleteVisit_Click(object sender, EventArgs e)
      {
         if (MessageBox.Show(this, "Вы хотите удалить Визит и все его документы?",
            "Внимание", MessageBoxButtons.OKCancel) == DialogResult.OK)
         {
            OrderDetailRepresentation odr = dgvDetail.CurrentRow.DataBoundItem as OrderDetailRepresentation;

            SimpleDataSet<Visit> rmv = new SimpleDataSet<Visit>(Visit.OBJECT_NAME, false);
            SimpleDataSet<Answer> rma = new SimpleDataSet<Answer>(Answer.OBJECT_NAME, false);
            SimpleDataSet<Purchase> rmp = new SimpleDataSet<Purchase>(Purchase.OBJECT_NAME, false);
            SimpleDataSet<Selling> rms = new SimpleDataSet<Selling>(Selling.OBJECT_NAME, false);
            SimpleDataSet<ScriptDoc> rmscr = new SimpleDataSet<ScriptDoc>(ScriptDoc.OBJECT_NAME, false);

            ScriptDoc scr = FindScript(odr);

            if (scr == null) return;

            NeedRemove nr = new NeedRemove();
            nr.docCreated = scr.created;
            nr.userid = scr.userid;
            nr.created = DateTime.Now;

            SimpleDataSet<NeedRemove> dsNR = new SimpleDataSet<NeedRemove>(NeedRemove.OBJECT_NAME, false);
            dsNR.Add(nr);

            rmscr.Add(scr);

            if (!scr.visitDoc.Equals(DateTime.MinValue))
            {
               Visit v = new Visit();
               v.userid = scr.userid;
               v.created = scr.visitDoc;
               rmv.Add(v);
            }

            foreach (ScriptDocItem i in scr.items)
            {
               if (i.type.Equals(Answer.OBJECT_NAME))
               {
                  Answer a = new Answer();
                  a.created = i.date;
                  a.userid = scr.userid;
                  rma.Add(a);
               }
               else if (i.type.Equals(Purchase.OBJECT_NAME))
               {
                  Purchase p = new Purchase();
                  p.created = i.date;
                  p.userid = scr.userid;
                  rmp.Add(p);
               }
               else if (i.type.Equals(Selling.OBJECT_NAME))
               {
                  Selling h = new Selling();
                  h.created = i.date;
                  h.userid = scr.userid;
                  rms.Add(h);
               }
            }

            if (rmscr.Count == 0) { return; }

            List<IDataSet> write = new List<IDataSet>();
            List<IDataSet> remove = new List<IDataSet>();

            SimpleDataSet<NeedRemove> ds = new SimpleDataSet<NeedRemove>(NeedRemove.OBJECT_NAME, false);
            ds.Add(nr);
            write.Add(ds);
            remove.Add(rmscr);
            remove.Add(rma);
            remove.Add(rmv);
            remove.Add(rms);
            remove.Add(rmp);

            SimpleDataSet<NeedRemove> remOldNR = new SimpleDataSet<NeedRemove>(NeedRemove.OBJECT_NAME, false);
            remOldNR.Filter = string.Format("\"created\"<=ToDate('{0:dd/MM/yyyy}')", DateTime.Now.AddDays(-3));
            DataModule.RemoveDataSet(remOldNR, Config.GetConfig().GetConnection());

            Config cfg = Config.GetConfig();

            if (DataModule.UpdateDataSet(write, remove, null, cfg.GetConnection()))
               DialogUtil.SavedGood(this);
            else
               DialogUtil.UpdateErrMsg(this);

            btnRefresh.PerformClick();
         }
      }

      protected override void lblAdress_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
      {
         OrderDetailRepresentation odr = dgvDetail.CurrentRow.DataBoundItem as OrderDetailRepresentation;
         OrgLocation loc = new OrgLocation();
         loc.id = odr.NOrg.id;
         loc.latitude = odr.NOrg.latitude;
         loc.longitude = odr.NOrg.longitude;

         FmAddrShow.AddrShow(new Location(loc.latitude, loc.longitude), odr.NOrg);
      }

      protected override void BeforeRefreshData(List<IDataSet> updSets, string agentID, DateTime dateBegin, DateTime dateEnd)
      {
         base.BeforeRefreshData(updSets, agentID, dateBegin, dateEnd);

         dsPurchase.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsSelling.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         dsPicStore.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
         updSets.Add(dsPurchase); 
         updSets.Add(dsSelling);
         updSets.Add(dsPicStore);

         dsOrderCommitted.Filter = String.Format(COMMON_FILTER_STR, "created", dateBegin, dateEnd, agentID);
      }

      protected override void UpdateGrid(bool refreshFilterCB)
      {
         base.UpdateGrid(refreshFilterCB);

         tsslSum.Text = String.Format("Сумма документов Закуп: {0}:",
               oDetail.Sum.ToString("C", Config.GetCultureInfo()));
      }

      protected override string TotalCount()
      {
         return "Всего визитов: " + dsScriptDoc.Count;
      }

      protected override void SetScriptInfo(ScriptDoc sd)
      {
         base.SetScriptInfo(sd);

         List<PicStore> pics = new List<PicStore>();

         if (sd != null)
         {
            foreach(ScriptDocItem item in sd.items)
            {
               if (item.type.Equals("Answer"))
               {
                  foreach (PicStore ps in dsPicStore.Values)
                  {
                     if (ps.date.Equals(item.date))
                        pics.Add(ps);
                  }
               }
            }
         }

         if (pics.Count == 0)
            return;

         StringBuilder htmlBuilder = new StringBuilder();
         bool addBr = false;
         StartPhotoHTML(htmlBuilder);

         string docDate = sd.created.ToString("dd.MM.yy HH:mm");
         int count = 0;

         foreach (PicStore ps in pics)
         {
            count++;
            String tag = count.ToString();

            if (AddPhotoToHtml(htmlBuilder, tag, ps.name, ps.smallName, ps.smallSize, docDate, ps.created))
               addBr = true;
         }

         if (addBr)
            htmlBuilder.Append("<br/>");

         UpdateWebHtml(htmlBuilder);
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
      internal override Control RefreshDetail(OrderDetailRepresentation odr)
      {
         Control result = null;

         DocView dv = GetDocView(Enum.GetName(typeof(ObjType.TObjType), odr.Doctype.Val));

         if (dv != null)
         {
            result = FindDetailControl(dv); ;

            if (result == null)
            {
               result = dv.MakeControl();
               detailPanel.Controls.Add(result);
               result.Dock = DockStyle.Fill;
            }

            if (result is DataObjectViewer)
               ((DataObjectViewer)result).SetData(odr.StoreObject);

            result.Visible = true;
         }

         return result;
      }

      // для дублирования надо удалить OrderCommitted
      protected override IDataSet GetDuplicate(Network.DataObject dataObject)
      {
         SimpleDataSet<OrderCommitted> oc = new SimpleDataSet<OrderCommitted>(OrderCommitted.OBJECT_NAME, false);
         Purchase pd = dataObject as Purchase;
         Selling sd = dataObject as Selling;
         if (pd != null)
         {
            OrderCommitted d = new OrderCommitted();
            d.userid = pd.userid;
            d.created = pd.created;
            oc.Add(d);
         }
         else if (sd != null)
         {
            OrderCommitted d = new OrderCommitted();
            d.userid = sd.userid;
            d.created = sd.created;
            oc.Add(d);
         }

         return oc.Count > 0 ? oc : null;
      }

      public override bool DocCanMissed(BaseDocument doc)
      {
         return base.DocCanMissed(doc) || doc is Purchase;
      }

      void RemoveCommitInfo(OrderCommitted src)
      {
         foreach (KeyValuePair<int, OrderCommitted> kv in dsOrderCommitted)
         {
            if (kv.Value.created == src.created && kv.Value.userid == src.userid)
            {
               dsOrderCommitted.Remove(kv.Key);
               return;
            }
         }
      }

      protected override void miMakeDup_Click(object sender, EventArgs e)
      {
         IDataSet dup = GetDupDataSet();
         if( dup != null)
         {
            List<IDataSet> rmv = new List<IDataSet>();
            rmv.Add(dup);
            Config cfg = Config.GetConfig();

            if(DataModule.UpdateDataSet(null, rmv, null, cfg.GetConnection(), GetSelectedIdAgent()))
            {
               foreach(object o in dup.Data)
               {
                  OrderCommitted oc = o as OrderCommitted;
                  if(oc != null)
                  {
                     RemoveCommitInfo(oc);
                  }
               }
            }

            //if (DataModule.UpdateDataSet(null, rmv, null, cfg.GetConnection(), GetSelectedIdAgent()))
            //   MessageBox.Show("Операция завершена успешно", "Информация", MessageBoxButtons.OK,
            //      MessageBoxIcon.Information);
            //else
            //   MessageBox.Show("Ошибка записи в базу данных", "Ошибка", MessageBoxButtons.OK,
            //      MessageBoxIcon.Error);
         }
      }

      protected override bool CanDuplicate(Network.DataObject dataObject)
      {
         Manager mc = CurrentUser.user as Manager;
         if (mc != null)
         {
            bool res = !mc.HaveRight(RightTokens.Get("DisableCopy"), RightActions.Write);

            return res;
         }

         return false;
      }


      protected bool CanRemove()
      {
         Manager mc = CurrentUser.user as Manager;
         if (mc != null)
         {
            bool res = !mc.HaveRight(RightTokens.Get("DisableDelete"), RightActions.Write);

            return res;
         }

         return false;
      }

      protected bool CanPreview()
      {
         Manager mc = CurrentUser.user as Manager;
         if (mc != null)
         {
            bool res = !mc.HaveRight(RightTokens.Get("DisableLook"), RightActions.Write);

            return res;
         }

         return false;
      }

      protected override void ShowPhoto(Image photo, string tag)
      {
         if (CanPreview())
            base.ShowPhoto(photo, tag);
      }
      protected override void cmDgvDetail_Opening(object sender, CancelEventArgs e)
      {
         if (dgvDetail.CurrentRow == null || miModeScript.Checked)
         {
            e.Cancel = true;
            return;
         }

         miMakeDup.Visible = GetDupDataSet() != null && DocHaveSum();

         OrderDetailRepresentation odr = dgvDetail.CurrentRow.DataBoundItem as OrderDetailRepresentation;

         if (odr != null && odr.dataObject != null)
         {
            itDeleteDoc.Visible = odr.dataObject is Purchase || odr.dataObject is Selling;
            miMakeDup.Text = odr.dataObject is Selling ? "Экспорт в УТ" : "Экспорт в ERP";
         }
         else
            itDeleteDoc.Visible = false;
      }

      private bool DocHaveSum()
      {
         OrderDetailRepresentation odr = dgvDetail.CurrentRow.DataBoundItem as OrderDetailRepresentation;

         if (odr == null && odr.dataObject == null)
            return false;

         if (odr.DblSum > 0)
            return true;

         return false;
      }
   }

}
