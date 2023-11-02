using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Windows.Media;
using System.Windows.Media.Imaging;

namespace Ads2017
{
    public class JournalItem
    {
        public string number = string.Empty;
        public string user = string.Empty;
        public DateTime created = DateTime.MinValue;
        public string timeplan = string.Empty;
        public string timefact = string.Empty;
        public string task = string.Empty;
        public string client = string.Empty;
        public string address = string.Empty;
        public string status = string.Empty;
        public string report = string.Empty;
        public string taskid = string.Empty;
        public Color statusColor = Colors.Black;
        public int photoCount = 0;

        public ObservableCollection<BitmapImage> images = new ObservableCollection<BitmapImage>();

        public string Number { get { return number; } }
        public string User { get { return user; } }
        public DateTime Created { get { return created; } }
        public string TimePlan { get { return timeplan; } }
        public string TimeFact { get { return timefact; } }
        public string Task { get { return task; } }
        public string Client { get { return client; } }
        public string Address { get { return address; } }
        public string Status { get { return status; } }
        public string Report { get { return report; } }
        public ObservableCollection<BitmapImage> Images { get { return images; } }

        public BitmapImage Image { get; set; }
    }

}
