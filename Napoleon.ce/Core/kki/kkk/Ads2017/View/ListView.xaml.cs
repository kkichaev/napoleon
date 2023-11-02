using System;
using System.Windows.Controls;
using System.Windows.Input;

namespace Ads2017
{
   public partial class ListView : UserControl
   {
      public delegate void OnItemClickHandler(object sender, object item);

      public OnItemClickHandler OnItemClick { get; set; }
      public OnItemClickHandler OnItemDoubleClick { get; set; }
      private ListViewAdapter adapter = null;

      
      public ListView()
      {
         InitializeComponent();
      }

      public ListViewAdapter Adapter
      {
         get { return adapter; }
         set { ApplyAdapter(value); }
      }

      private int selectedIndex = -1;
      public int SelectedIndex
      {
         get { return selectedIndex; }
         set { SetSelection(value); }
      }

      private void SetSelection(int idx)
      {
         if (idx >= 0 && panel.Children.Count > idx)
         {
            ResetSelection();

            if (panel.Children[idx] is IListViewItem i)
            {
               i.Selected = true;
               FireOnItemClick(i);
            }
         }
      }

      private void ApplyAdapter(ListViewAdapter newAdapter)
      {
         adapter = newAdapter;
         UpdateListView();
      }

      private void UpdateListView()
      {
         if (adapter != null)
         {
            panel.Children.Clear();

            for (int i = 0; i < adapter.Count; i++)
            {
               UserControl c = adapter.GetView(i);
               c.MouseDown += ItemClick;
               c.MouseDoubleClick += ItemDoubleClick;

               panel.Children.Add(c);
            }
         }
      }

      private void ItemDoubleClick(object sender, MouseButtonEventArgs e)
      {
         if (sender is UserLocationTicket item)
            FireOnItemDoubleClick(item);
      }

      private void FireOnItemDoubleClick(UserLocationTicket item)
      {
         OnItemDoubleClick?.Invoke(this, item);
      }

      private void ItemClick(object sender, MouseButtonEventArgs e)
      {
         ResetSelection();

         if (sender is IListViewItem i)
            i.Selected = true;

         FireOnItemClick(sender);
      }

      private void ResetSelection()
      {
         foreach (object o in panel.Children)
         {
            if (o is IListViewItem i)
               i.Selected = false;
         }
      }

      private void FireOnItemClick(object item)
      {
         OnItemClick?.Invoke(this, item);
      }
   }

   public abstract class ListViewAdapter
   {
      public abstract object GetItem(int position);
      public abstract UserControl GetView(int position);
      public abstract int Count { get; }
   }
}
