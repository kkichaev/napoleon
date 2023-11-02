using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Threading;
using GRSoft.NapoleonManager.Properties;
using System.Reflection;

namespace GRSoft.NapoleonManager
{
   public partial class FmScriptEdit : Form
   {
      protected ScriptDef script;
      private PostProcess postProcess;
      bool isReadonly = false;
      
#if QUESTION_IN_SCRIPTS
      const string QUEST_TYPE = "Answer";

      DataSet<string, Question> dsQuestion = (DataSet<string, Question>)DataModule.Get(Question.OBJECT_NAME) ??
            new DataSet<string, Question>(Question.OBJECT_NAME);
#endif

      public delegate void PostProcess(int id);

      public static void ShowModal(PostProcess pp)
      {
         ShowModal(null, pp, false);
      }

      public static void ShowModal(ScriptDef script, PostProcess pp, bool readOnly)
      {
         Type prcType = FormEntries.GetFormType(typeof(FmScriptEdit));
         ConstructorInfo ci = prcType.GetConstructor(BindingFlags.Instance | BindingFlags.NonPublic, null, new Type[] { typeof(PostProcess) }, null);
         FmScriptEdit fm = (FmScriptEdit)ci.Invoke(new object[] {pp});
         fm.script = script;
         fm.SetReadOnly(readOnly);
         fm.ShowDialog();
      }

      public void SetReadOnly(bool ro)
      {
         isReadonly = ro;
         if(isReadonly)
         {
            MarkDirty(false);
            lblInfo.Text = "Редактирование запрещено - есть документы по сценарию";
         } else
         {
            lblInfo.Text = "";
         }
      }

      public virtual void __Initing(PostProcess postProcess)
      {
         this.postProcess = postProcess;

#if QUESTION_IN_SCRIPTS
         List<IDataSet> list = new List<IDataSet>();
         list.Add(dsQuestion);
         DateTime now = DateTime.Now;
         const string SELECT_FOR_RANGE = "params & 1 = 0 or  (params & 1 = 1 and \"from\" <=  ToDate('{0:dd/MM/yyyy}' and \"till\" >= ToDate('{0:dd/MM/yyyy}'))";
         dsQuestion.Filter = String.Format(SELECT_FOR_RANGE, now);
         Thread t = DataModule.RefreshGiveSets(Config.GetConfig().GetConnection(), list, null);
         t.Join();
#endif
         int idx = 0;
         foreach( ScriptDocument sd in ScriptDocuments.Documents )
         {
            imageList1.Images.Add(sd.icon);
            ListViewItem item = lvDocsAvail.Items.Add(sd.type, sd.name, idx);
            item.Tag = sd;
            idx++;
         }

#if QUESTION_IN_SCRIPTS
         foreach (Question quest in dsQuestion.Data)
         {
            QuestionDoc doc = new QuestionDoc(quest.idquest);
            imageList1.Images.Add(Resources.quest_doc);
            ListViewItem item = lvDocsAvail.Items.Add(QUEST_TYPE, String.Format("Анкета {0}", quest.Name), idx);
            item.Tag = doc;
            idx++;
         }
#endif
         DecoratorFactory.GetDecorator(this);
      }

      private void btnAdd_Click(object sender, EventArgs e)
      {
         AddScriptItem();
      }

      protected virtual void AddScriptItem()
      {
         foreach (ListViewItem item in lvDocsAvail.SelectedItems)
         {
            ListViewItem clone = (ListViewItem)item.Clone();
            clone.Checked = true;
            lvDocs.Items.Add(clone);
         }

         if (tbName.Text.Trim().Length == 0 && lvDocs.Items.Count > 0)
            tbName.Text = lvDocs.Items[0].Text;

         MarkDirty(true);
      }

      void MarkDirty(bool dirty)
      {
         btnSave.Enabled = (!isReadonly) && dirty;
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         foreach (ListViewItem item in lvDocs.SelectedItems)
            lvDocs.Items.Remove(item);

         MarkDirty(true);
      }

      private void btnUp_Click(object sender, EventArgs e)
      {
         MoveDocsItem(new CanMoveItem(delegate(int pos) { return pos > 0; }),
               new NewPosIndex(delegate(int pos) { return --pos; }));
      }

      private void btnDown_Click(object sender, EventArgs e)
      {
         MoveDocsItem(new CanMoveItem(delegate(int pos) { return pos < lvDocs.Items.Count - 1; }),
               new NewPosIndex(delegate(int pos) { return ++pos; }));
      }

      private delegate bool CanMoveItem(int pos);
      private delegate int NewPosIndex(int pos);

      private void MoveDocsItem(CanMoveItem canMoveItem, NewPosIndex newPosIndex)
      {
         if (canMoveItem != null && newPosIndex != null &&
            lvDocs.SelectedItems.Count == 1)
         {
            ListViewItem item = lvDocs.SelectedItems[0];
            int pos = item.Index;

            if (canMoveItem(pos))
            {
               lvDocs.Items.Remove(item);
               lvDocs.Items.Insert(newPosIndex(pos), item);
            }
         }

         MarkDirty(true);
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         Save();
      }

      protected virtual void BeforeSaveItem(ScriptDefItem scriptItem, ListViewItem item) { }
      protected virtual void BeforeSaveDef() { }

      protected virtual void Save()
      {
         if (lvDocs.Items.Count > 0)
         {
            if (script == null)
               script = new ScriptDef();

            script.name = tbName.Text;
            script.items = new List<ScriptDefItem>();

            BeforeSaveDef();

            for (int i = 0; i < lvDocs.Items.Count; i++)
            {
               ListViewItem item = lvDocs.Items[i];
               ScriptDefItem scriptItem = new ScriptDefItem();
               ScriptDocument scriptDoc = item.Tag as ScriptDocument;

               if (script != null)
               {
                  scriptItem.condition = item.Checked ? 1 : 0;
                  scriptItem.curType = scriptDoc.type;
                  scriptItem.name = item.Text;
                  scriptItem.nextDoc = (i == lvDocs.Items.Count - 1) ? -1 : i + 1;
                  scriptItem.condParam = scriptDoc.condParam;
                  scriptItem.pos = i + 1;

                  if (scriptItem.id.Length == 0)
                     scriptItem.id = GRSoft.Network.DataObject.GenId();

                  BeforeSaveItem(scriptItem, item);
                  script.items.Add(scriptItem);
               }
            }

            DataSet<int, ScriptDef> ins = new DataSet<int, ScriptDef>(ScriptDef.OBJECT_NAME, false);
            ins.Add(0, script);

            List<IDataSet> insDs = new List<IDataSet>();
            insDs.Add(ins);

            if (script.id == -1)
               DataModule.InsertDataSets(insDs, Config.GetConfig().GetConnection());
            else
               DataModule.UpdateDataSet(insDs, null, null, Config.GetConfig().GetConnection());

            MarkDirty(false);
         }
      }

      protected virtual void LoadData()
      {
         if (script != null)
         {
            tbName.Text = script.Name;

            foreach (ScriptDefItem item in script.items)
            {
               ListViewItem lvItem = GetItemByType(item);

               if (lvItem != null)
               {
                  ScriptDocument sd = lvItem.Tag as ScriptDocument;
                  if (sd != null)
                  {
                     ListViewItem i = lvDocs.Items.Add(sd.ToString(), item.Name, lvItem.ImageIndex);
                     i.Checked = item.condition == 1;
                     i.Tag = lvItem.Tag;

                     SettingDocItem(item, i);
                  }
               }
            }
         }

         MarkDirty(false);
      }

      protected virtual void SettingDocItem(ScriptDefItem item, ListViewItem i)
      {
         
      }

      public void FmScriptEdit_Load(object sender, EventArgs e)
      {
         LoadData();
      }

      private ListViewItem GetItemByType(ScriptDefItem sdi)
      {
         ListViewItem result = null;

         if(sdi != null)
            foreach (ListViewItem item in lvDocsAvail.Items)
            {
               ScriptDocument sd = item.Tag as ScriptDocument;
               if (sd != null && sd.type.Equals(sdi.curType) 
                  && (!(sd is QuestionDoc) || (sd is QuestionDoc) && (sd.condParam.Length == 0 || sd.condParam.Equals(sdi.condParam))))
               {
                  result = item.Clone() as ListViewItem;
                  break;
               }
            }

         return result;
      }

      private void tbName_TextChanged(object sender, EventArgs e)
      {
         MarkDirty(true);
      }

      private void FmScriptEdit_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (btnSave.Enabled && 
            MessageBox.Show("Сохранить изменения?", "Вопрос", 
               MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK)
         {
            btnSave_Click(null, null);
         }
      }

      private void FmScriptEdit_FormClosed(object sender, FormClosedEventArgs e)
      {
         if (postProcess != null && script != null)
            postProcess(script.id);
      }

      private void lvDocs_ItemChecked(object sender, ItemCheckedEventArgs e)
      {
         if (lvDocs.FocusedItem != null)
         {
            MarkDirty(true);
         }
      }
   }
}
