using System.Collections.ObjectModel;
using System.IO;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media.Imaging;

namespace Ads2017
{
    /// <summary>
    /// Interaction logic for PhotoWindow.xaml
    /// </summary>
    public partial class PhotoWindow : Window
    {
        public PhotoWindow()
        {
            InitializeComponent();
        }

        private void Image_MouseLeftButtonDown(object sender, MouseButtonEventArgs e)
        {
            Image img = (Image)sender;

            if (img.Source is BitmapImage bi)
            {
                var encoder = new PngBitmapEncoder();
                encoder.Frames.Add(BitmapFrame.Create(bi));

                string name = string.Format("{0}.png", Path.GetTempFileName());

                using (var stream = File.Create(name))
                {
                    encoder.Save(stream);
                }

                System.Diagnostics.Process.Start(name);
            }
        }

        public ObservableCollection<BitmapImage> Images { get; set; }
    }
}
