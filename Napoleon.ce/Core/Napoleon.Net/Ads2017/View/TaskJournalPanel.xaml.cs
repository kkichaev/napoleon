using System;
using System.Collections;
using System.Collections.Generic;
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
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace Ads2017
{
    public partial class TaskJournalPanel : UserControl
    {
        public delegate void SelectTaskJournal(object sender, JournalItem item);
        public event SelectTaskJournal OnSelectTaskItem;

        public TaskJournalPanel()
        {
            InitializeComponent();
        }

        object source = null;
        object currentItem = null;

        public object ItemsSource
        {
            get { return source; }
            set { SetItemsSource(value); }
        }

        public JournalItem CurrentItem
        {
            get { return (JournalItem)currentItem; }
            set { currentItem = value; }
        }

        internal void SelectItem(TaskJournalView currentItem)
        {
            CurrentItem = (JournalItem)currentItem.StoredObject;
            SetBackgroundForSelection(currentItem);
            FireOnSelectItem(CurrentItem);
        }

        private void SetBackgroundForSelection(TaskJournalView currentItem)
        {
            foreach (UIElement c in panel.Children)
            {
                if (c is TaskJournalView view)
                {
                    view.Background = new SolidColorBrush(Colors.White);
                }
            }

            currentItem.Background = new SolidColorBrush(Colors.LightBlue);
        }

        private void FireOnSelectItem(JournalItem currentItem)
        {
            OnSelectTaskItem?.Invoke(this, currentItem);
        }

        private void SetItemsSource(object value)
        {
            AddToPanel(value);
            source = value;
        }

        private void AddToPanel(object value)
        {
            panel.Children.Clear();

            if (value is IEnumerable en)
            {
                foreach (object o in en)
                {
                    if (o is JournalItem p)
                    {
                        TaskJournalView view = new TaskJournalView()
                        {
                            StoredObject = p,
                            User = p.User,
                            Created = p.Created,
                            TimePlan = p.TimePlan,
                            TimeFact = p.TimeFact,
                            Text = p.Task,
                            Address = p.Address,
                            Client = p.Client,
                            Status = p.Status,
                            Images = p.Images,
                            Remark = p.Report,
                            StatusColor = p.statusColor.ToString(),
                            Container = this
                        };

                        panel.Children.Add(view);
                    }
                }
            }
        }
    }
}
