using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Web.Script.Serialization;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;

namespace Ads2017
{
   /// <summary>
   /// Interaction logic for TemplateWindow.xaml
   /// </summary>
   public partial class TemplateWindow : Window, Update.IDataLoadProcess
   {
      private Dictionary<string, TemplateData> update = new Dictionary<string, TemplateData>();
      private Dictionary<string, TemplateData> delete = new Dictionary<string, TemplateData>();
      private ObservableCollection<TemplateData> data = new ObservableCollection<TemplateData>();
      private JavaScriptSerializer json = new JavaScriptSerializer();

      public TemplateWindow()
      {
         InitializeComponent();
         template.ItemsSource = data;

         CommandManager.InvalidateRequerySuggested();
      }

      private void AddTemplate(object sender, RoutedEventArgs e)
      {
         ObservableCollection<TemplateData> list = (ObservableCollection<TemplateData>)template.ItemsSource;
         TemplateData a = new TemplateData() { ID = GRSoft.Network.DataObject.GenId() };
         list.Insert(0,a);

         template.Focus();
         template.CurrentCell = new System.Windows.Controls.DataGridCellInfo(template.Items[0], template.Columns[0]);
         template.BeginEdit();

         CommandManager.InvalidateRequerySuggested();
      }

      private void RemoveTemplate(object sender, RoutedEventArgs e)
      {
         if (template.SelectedItem is TemplateData a)
         {
            ((ObservableCollection<TemplateData>)template.ItemsSource).Remove(a);
            delete[a.ID] = a;

            if (update.ContainsKey(a.ID))
               update.Remove(a.ID);
         }
      }

      private void TemplateCellEditEnding(object sender, DataGridCellEditEndingEventArgs e)
      {
         if (e.Row.Item is TemplateData a && e.EditAction == DataGridEditAction.Commit)
         {
            if (a.ID == null || a.ID.Trim().Length == 0)
               a.ID = GRSoft.Network.DataObject.GenId();

            update[a.ID] = a;
         }
      }

      private bool Save()
      {
         template.CommitEdit(DataGridEditingUnit.Row, true);
         address.CommitEdit(DataGridEditingUnit.Row, true);
         contact.CommitEdit(DataGridEditingUnit.Row, true);

         UpdateCollection write = new UpdateCollection();
         write.Add(AddressTemplate.OBJECT_NAME, ConvertData(update.Values));

         UpdateCollection remove = new UpdateCollection();
         remove.Add(AddressTemplate.OBJECT_NAME, ConvertData(delete.Values));

         return Update.WriteObjects(write, remove);
      }

      private List<AddressTemplate> ConvertData(IEnumerable<TemplateData> input)
      {
         List<AddressTemplate> result = new List<AddressTemplate>();

         foreach(TemplateData t in input)
         {
            string s = json.Serialize(t);
            result.Add(new AddressTemplate() { id = t.id, template = s});
         }

         return result;
      }

      private void CanSaveExecute(object sender, CanExecuteRoutedEventArgs e)
      {
         e.CanExecute = HasUnsavedData();
      }

      private bool HasUnsavedData()
      {
         return update.Count > 0 || delete.Count > 0;
      }

      private void RefreshExecuted(object sender, ExecutedRoutedEventArgs e)
      {
         Refresh();
      }

      private void Refresh()
      {
         Update.QueryList upd = new Update.QueryList();
         upd.Add(AddressTemplate.OBJECT_NAME);

         Update.StdDataRefresh(upd, this);
      }

      private void SaveExecuted(object sender, ExecutedRoutedEventArgs e)
      {
         bool result = Save();

         if (result)
         {
            update.Clear();
            delete.Clear();
            CommandManager.InvalidateRequerySuggested();
            StdDialog.SavedGood(this);
         }
         else
            StdDialog.UpdateErrMsg(this);
      }

      public void DoLoadData(Update.UpdateResult data)
      {
         List<AddressTemplate> list = data.GetList<AddressTemplate>(AddressTemplate.OBJECT_NAME);
         List<TemplateData> d = new List<TemplateData>();

         foreach (AddressTemplate a in list)
         {
            TemplateData td = (TemplateData)json.Deserialize(a.template, typeof(TemplateData));
            d.Add(td);
         }

         d.Sort((x, y) => { return  x.Name.CompareTo(y.Name); });

         this.data.Clear();
         foreach (TemplateData t in d)
            this.data.Add(t);

         update.Clear();
         delete.Clear();
      }

      public UIElement[] GetRefreshControls()
      {
         return new UIElement[] { btnSave, btnRefresh };
      }

      private void Window_Loaded(object sender, RoutedEventArgs e)
      {
         Refresh();
      }

      private void AddAddress(object sender, RoutedEventArgs e)
      {
         if (template.SelectedItem is TemplateData t)
         {
            TemplateAddress a = new TemplateAddress();

            t.Address.Insert(0, a);

            address.Focus();
            address.CurrentCell = new System.Windows.Controls.DataGridCellInfo(address.Items[0], address.Columns[0]);
            address.BeginEdit();

            update[t.ID] = t;
            CommandManager.InvalidateRequerySuggested();
         }
      }

      private void RemoveAddress(object sender, RoutedEventArgs e)
      {
         if (template.SelectedItem is TemplateData t && address.SelectedItem is TemplateAddress a)
         {
            ((ObservableCollection<TemplateAddress>)address.ItemsSource).Remove(a);
            update[t.ID] = t;
            CommandManager.InvalidateRequerySuggested();
         }
      }

      private void AddContact(object sender, RoutedEventArgs e)
      {
         if (template.SelectedItem is TemplateData t && address.SelectedItem is TemplateAddress a)
         {
            TemplateContact c = new TemplateContact();

            a.Contact.Insert(0, c);

            contact.Focus();
            contact.CurrentCell = new System.Windows.Controls.DataGridCellInfo(contact.Items[0], contact.Columns[0]);
            contact.BeginEdit();

            update[t.ID] = t;
            CommandManager.InvalidateRequerySuggested();
         }
      }

      private void RemoveContact(object sender, RoutedEventArgs e)
      {
         if (template.SelectedItem is TemplateData t && contact.SelectedItem is TemplateContact c)
         {
            ((ObservableCollection<TemplateContact>)contact.ItemsSource).Remove(c);
            update[t.ID] = t;
            CommandManager.InvalidateRequerySuggested();
         }
      }

      private void AddressCellEditEnding(object sender, DataGridCellEditEndingEventArgs e)
      {
         if (template.SelectedItem is TemplateData t && e.EditAction == DataGridEditAction.Commit)
         {
            update[t.ID] = t;
         }
      }

      private void ContactCellEditEnding(object sender, DataGridCellEditEndingEventArgs e)
      {
         if (template.SelectedItem is TemplateData t && e.EditAction == DataGridEditAction.Commit)
         {
            update[t.ID] = t;
         }
      }
   }
}
