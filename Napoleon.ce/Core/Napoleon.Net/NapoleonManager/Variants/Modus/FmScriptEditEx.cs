using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class FmScriptEditEx : FmScriptEdit
   {
      enum CheckResult 
      {
         Good,
         MerchStartMissing,
         MerchFinishMissing,
         TaskStartMissing,
         TaskFinishMissing,
         OrderInvalid
      }

      protected FmScriptEditEx(PostProcess postProcess):base(postProcess)
      {
      }

      protected override void Save()
      {
         CheckResult ss = ScriptIsGood();
         if (ss == CheckResult.Good)
            base.Save();
         else 
            MsgSaveErrror(ss);
      }

      private void MsgSaveErrror(CheckResult ss)
      {
         string text = "";
         switch (ss)
         {
            case CheckResult.MerchStartMissing:
               text = "Пропущен документ Мерч начало";
               break;
            case CheckResult.MerchFinishMissing:
               text = "Пропущен документ Мерч конец";
               break;
            case CheckResult.TaskStartMissing:
               text = "Пропущен документ Задачи начало";
               break;
            case CheckResult.TaskFinishMissing:
               text = "Пропущен документ Задачи конец";
               break;
            case CheckResult.OrderInvalid:
               text = "Нарушен порядок документов (документа начало должен идти перед документ конец)";
               break;
         }

         MessageBox.Show(this, text, "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
      }

      private CheckResult ScriptIsGood()
      {
         CheckResult result = CheckResult.Good;

         bool ms = false, me = false, io = false, ts = false, te = false;

         for (int i = 0; i < lvDocs.Items.Count; i++)
         {
            ListViewItem item = lvDocs.Items[i];

            if (!ms && item.Tag is MerchBeginDoc)
               ms = true;
            else if (!me && item.Tag is MerchEndDoc)
               me = true;
            else if (!ts && item.Tag is TaskBeginDoc)
               ts = true;
            else if (!te && item.Tag is TaskEndDoc)
               te = true;
            else if (!io && (!ms && me && item.Tag is MerchEndDoc) || (!ts && te && item.Tag is TaskEndDoc))
               io = true;
         }

         if (!ms && me)
            result = CheckResult.MerchStartMissing;
         else if (!me && ms)
            result = CheckResult.MerchFinishMissing;
         else if (!ts && te)
            result = CheckResult.TaskStartMissing;
         else if (!te && ts)
            result = CheckResult.TaskFinishMissing;
         else if (io)
            result = CheckResult.OrderInvalid;

         return result;
      }

      protected override void AddScriptItem()
      {
         base.AddScriptItem();

      }
   }
}
