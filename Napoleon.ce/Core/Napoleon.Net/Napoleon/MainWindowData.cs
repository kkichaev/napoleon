using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Napoleon
{
    public class MainWindowData : INotifyPropertyChanged
    {
        public string Net { get; set; }
        public string OrgName { get; set; }
        public string OrgAddress { get; set; }
        public string TPCode { get; set; }
        //public string Mon { get; set; }
        //public string Tue { get; set; }
        //public string Wed { get; set; }
        //public string Thu { get; set; }
        //public string Fri { get; set; }
        //public string Sat { get; set; }
        //public string Sun { get; set; }
        public string LastDlvSum { get; set; }
        public double LastDlvSumD { get; set; }
        //public string LastDlvWeight { get; set; }
        public double LastDlvWeightD { get; set; }
        public string DateTTN { get; set; }
        //public string DZ1 { get; set; }
        //public string DZ2 { get; set; }
        //public string DZ3 { get; set; }
        public string LastOrderSum { get; set; }
        public double LastOrderSumD { get; set; }
        //public string LastOrderWeight { get; set; }
        public double LastOrderWeightD { get; set; }
        string _remark = "";
        public string Remark { get => _remark; set { _remark = value; OnPropertyChanged("Remark"); } }
        string _ct = "";
        public string CellTime { get => _ct; set { _ct = value; OnPropertyChanged("CellTime"); } }

        public string OrgID { get; set; }

        string _text = "";
        public string Text { get => _text; set { _text = value; OnPropertyChanged("Text"); } }

        public Dictionary<string, Order> orders = new Dictionary<string, Order>();
        public Dictionary<string, Delivery> dlvs = new Dictionary<string, Delivery>();

        public event PropertyChangedEventHandler PropertyChanged;

        public void OnPropertyChanged(string prop = "")
        {
            if (PropertyChanged != null)
                PropertyChanged(this, new PropertyChangedEventArgs(prop));
        }


        public void SetFirm(string firm)
        {
            Order o = null;
            if(orders.TryGetValue(firm, out o))
            {
                LastOrderSumD = o.Sum;
                LastOrderWeightD = o.Weight;
            }
            else
            {
                LastOrderSumD = 0;
                LastOrderWeightD = 0;
            }
            LastOrderSum = LastOrderSumD == 0 ? "" : LastOrderSumD.ToString();

            Delivery d = null;
            if(dlvs.TryGetValue(firm, out d))
            {
                LastDlvSumD = d.Sum;
                LastDlvWeightD = d.Weight;
                DateTTN = d.Date.ToShortDateString();
            }
            else
            {
                LastDlvSumD = 0;
                LastDlvWeightD = 0;
                DateTTN = "";
            }
            LastDlvSum = LastDlvSumD == 0 ? "" : LastDlvSumD.ToString();
        }
    }
}
