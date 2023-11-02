using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class DialogUtil
   {
      public static readonly string TITLE_ERR = "Ошибка";
      private static readonly string TITLE_QST = "Вопрос";
      private static readonly string TITLE_INFM = "Информация";

      public static bool AskToSave(IWin32Window owner)
      {
         const string ASK_TO_SAVE = "Сохранить изменения";
         return MessageBox.Show(owner, ASK_TO_SAVE, TITLE_QST, MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK;
      }

      public static bool AskToDel(IWin32Window owner)
      {
         const string ASK_TO_DEL_ROW = "Запись будет удалена, удалить?";
         return MessageBox.Show(owner, ASK_TO_DEL_ROW, TITLE_QST, MessageBoxButtons.OKCancel, MessageBoxIcon.Question) == DialogResult.OK;
      }

      public static void UpdateErrMsg(IWin32Window owner)
      {
         const string MSG = "Ошибка записи в базу данных";
         MessageBox.Show(owner, MSG, TITLE_ERR, MessageBoxButtons.OK, MessageBoxIcon.Error);
      }

      public static void SavedGood(IWin32Window owner)
      {
         const string MSG = "Изменения сохранены";
         MessageBox.Show(owner, MSG, TITLE_INFM, MessageBoxButtons.OK, MessageBoxIcon.Information);
      }
   }
}
