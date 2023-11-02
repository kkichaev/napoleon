using System;
using System.Collections.Generic;
using System.Text;
using System.Windows;

namespace Napoleon
{
   public partial class StdDialog
   {
      public static readonly string TITLE_ERR = "Ошибка";
      private static readonly string TITLE_QST = "Вопрос";
      private static readonly string TITLE_INFM = "Информация";

      public static bool AskToSave(Window owner)
      {
         const string ASK_TO_SAVE = "Сохранить изменения";
         return MessageBox.Show(owner, ASK_TO_SAVE, TITLE_QST, MessageBoxButton.OKCancel, MessageBoxImage.Question) == MessageBoxResult.OK;
      }

      public static bool AskToDel(Window owner)
      {
         const string ASK_TO_DEL_ROW = "Запись будет удалена, удалить?";
         return MessageBox.Show(owner, ASK_TO_DEL_ROW, TITLE_QST, MessageBoxButton.OKCancel, MessageBoxImage.Question) == MessageBoxResult.OK;
      }

      public static void UpdateErrMsg(Window owner)
      {
         const string MSG = "Ошибка записи в базу данных";
         MessageBox.Show(owner, MSG, TITLE_ERR, MessageBoxButton.OK, MessageBoxImage.Error);
      }

      public static void SavedGood(Window owner)
      {
         const string MSG = "Изменения сохранены";
         MessageBox.Show(owner, MSG, TITLE_INFM, MessageBoxButton.OK, MessageBoxImage.Information);
      }

        public static bool OrderWillClear(Window owner)
        {
            const string MSG = "Заявка будет очищена";
            return MessageBox.Show(owner, MSG, TITLE_QST, MessageBoxButton.OKCancel, MessageBoxImage.Question) == MessageBoxResult.OK;
        }
    }
}
