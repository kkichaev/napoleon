using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;

namespace Napoleon
{
    /// <summary>
    /// Логика взаимодействия для DataFilterDialog.xaml
    /// </summary>
    public partial class DataFilterDialog : Window
    {
        public DataFilterDialog()
        {
            InitializeComponent();
        }

        public List<string> Checked { get; set; }
        public ObservableCollection<DataItem> Items { get; private set; }

        public List<string> GetNewChecked
        {
            get
            {
                List<string> ret = new List<string>();
                Items.ForEach(x => {
                    if (x.Checked)
                        ret.Add(x.ID);
                });
                return ret;
            }
        }

        public static List<string> Filtering(List<IDataFiltrable> src, List<string> isChecked)
        {
            List<string> ret = null;

            DataFilterDialog dlg = new DataFilterDialog();
            dlg.Checked = isChecked;
            dlg.SetSrc(src);

            dlg.ShowDialog();

            if(dlg.DialogResult.HasValue && dlg.DialogResult.Value)
            {
                ret = dlg.GetNewChecked;
            }

            return ret;
        }

        private void SetSrc(List<IDataFiltrable> src)
        {
            Items = new ObservableCollection<DataItem>();
            src.ForEach(x =>
            {
                DataItem di = new DataItem(x, Checked.Contains(x.GetId));
                Items.Add(di);
            });
            list.ItemsSource = Items;
        }

        public class DataItem
        {
            IDataFiltrable data;

            public DataItem(IDataFiltrable data, bool isChecked)
            {
                this.data = data;
                Checked = isChecked;
            }

            public bool Checked { get; set; }

            public string Name { get => data.GetName; }
            public string ID { get => data.GetId; }
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            DialogResult = true;
        }
    }
}
