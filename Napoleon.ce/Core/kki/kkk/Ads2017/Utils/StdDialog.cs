using System;
using System.Collections.Generic;
using System.Resources;
using System.Text;
using System.Windows;

namespace Ads2017
{
   public partial class StdDialog
   {
      public static MessageBoxResult AskToSave(Window owner)
      {
         MessageBoxResult res = MessageBox.Show(owner, 
            ((App)Application.Current).resource.GetString("ask_to_save"), 
            ((App)Application.Current).resource.GetString("question"), 
            MessageBoxButton.YesNoCancel, MessageBoxImage.Question);
            return res;
      }

      public static bool AskToDel(Window owner)
      {
         return MessageBox.Show(owner,
             ((App)Application.Current).resource.GetString("ask_to_delete"), 
             ((App)Application.Current).resource.GetString("question"), 
             MessageBoxButton.OKCancel, MessageBoxImage.Question) == MessageBoxResult.OK;
      }

      public static void UpdateErrMsg(Window owner)
      {
         MessageBox.Show(owner, 
            ((App)Application.Current).resource.GetString("save_error"),
            ((App)Application.Current).resource.GetString("error"),
            MessageBoxButton.OK, MessageBoxImage.Error);
      }

      public static void SavedGood(Window owner)
      {
         MessageBox.Show(owner, 
            ((App)Application.Current).resource.GetString("save_complete"),
            ((App)Application.Current).resource.GetString("information"), 
            MessageBoxButton.OK, MessageBoxImage.Information);
      }
   }
}
