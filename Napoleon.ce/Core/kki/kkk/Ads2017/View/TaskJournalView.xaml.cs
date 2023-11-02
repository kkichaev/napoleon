using System;
using System.Collections.ObjectModel;
using System.IO;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;

namespace Ads2017
{
    public partial class TaskJournalView : UserControl
    {
        private TaskJournalPanel container;

        public TaskJournalView()
        {
            InitializeComponent();
        }

        private void Image_MouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            e.Handled = true;
            if (e.ChangedButton == MouseButton.Left && e.ClickCount == 2)
            {
                Image img = (Image)sender;

                if (img.Source is BitmapImage bi)
                {
                    var encoder = new PngBitmapEncoder();
                    encoder.Frames.Add(BitmapFrame.Create(bi));
                    string name = string.Format(@"{0}.png", Path.GetTempFileName());

                    using (var stream = File.Create(name))
                    {
                        encoder.Save(stream);
                    }

                    System.Diagnostics.Process.Start(name);
                }
            }
        }

        public string User { get; set; }
        public DateTime Created { get; set; }
        public string TimePlan { get; set; }
        public string TimeFact { get; set; }
        public string Text { get; set; }
        public string Client { get; set; }
        public string Address { get; set; }
        public string Status { get; set; }
        public string Remark { get; set; }
        public string StatusColor { get; set; }

        public TaskJournalPanel Container
        {
            get { return container; }
            set { container = value; }
        }

        public object StoredObject { get; set; }

        public ObservableCollection<BitmapImage> Images { get; set; }

        private void UserControl_MouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            container.SelectItem(this);
        }

        private void UserControl_SizeChanged(object sender, System.Windows.SizeChangedEventArgs e)
        {
            imagesControl.Width = ActualWidth; ;
        }
    }
}
