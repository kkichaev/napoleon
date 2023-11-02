using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading;
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
    public partial class PhoneActionWindow : Window, IWindowListener
    {
        public PhoneAction document;
        public DateTime DateDocument {  get { return document.changes; } }
        public DateTime DeliveryDate { get => document.date; set => document.date = value; }
        public string TPCode { get; set; }
        public IWindowListener WindowListener { get; set; }
        public string OrgID { get { return document.id; } }
        public string OrgName { get; set; }
        public string OrgAddress { get; set; }
        public string AgentName { get; set; }
        public string AgentPhone { get; set; }
        public List<Org.OrgContact> Contacts { get; set; }
        public string ContactFIO { get; set; }
        private bool docIninted = false;
        public string RC{ get; set; }
        public string FIO { get; set; }
        public string Text { get; set; }
        public string Remark { get; set; }
        private bool apply = false;

        public static DependencyProperty RejectCauseListProperty = DependencyProperty.Register("RejectCauseList",
            typeof(List<RejectCause>), typeof(PhoneActionWindow), new PropertyMetadata());

        public List<RejectCause> RejectCauseList
        {
            get { return (List<RejectCause>)GetValue(RejectCauseListProperty); }
            set { SetValue(RejectCauseListProperty, value); }
        }

        public PhoneActionWindow(MainWindowData d)
        {
            CreateOrFindDocument(d);
            InitilizeFromDocument();
            InitializeComponent();
            RejectCauseList = Update.GetStoredList<RejectCause>(RejectCause.OBJECT_NAME);
        }

        private void InitilizeFromDocument()
        {
            Contacts = new List<Org.OrgContact>();

            Dictionary<string, Org> orgs = Update.GetStoredDictionary<Org>(Org.COMMON_OBJECT_NAME);

            if (orgs.ContainsKey(document.id))
            {
                Org org = orgs[document.id];
                OrgName = org.Name;
                OrgAddress = org.Address;

                Agent a = org.agent;
                if (a != null)
                {
                    AgentName = a.name;
                    AgentPhone = a.phone;
                }

                org.contacts.ForEach((o) => { Contacts.Add(o); });

                if (document.contactFIO.Length == 0 && org.contacts.Count > 0)
                {
                    Org.OrgContact c = org.contacts[0];
                    document.contactFIO = c.name;
                    document.contactPHONE = c.phone;
                }
            }

            ContactFIO = document.contactFIO;

            Dictionary<string, Agent> agents = Update.GetStoredDictionary<Agent>(Agent.OBJECT_NAME);

            if (document.tpcode != null && agents.ContainsKey(document.tpcode))
            {
                Agent a = agents[document.tpcode];
                TPCode = string.Format("{0}({1})", a.Name, a.id);
            }

            RC = document.rejectCause;
            FIO = document.fio;
            Text = document.text;
            Remark = document.remark;

            docIninted = true;
        }

        private void CreateOrFindDocument(MainWindowData d)
        {
            foreach (PhoneAction p in Update.GetStoredList<PhoneAction>(PhoneAction.OBJECT_NAME))
            {
                if (p.id == d.OrgID)
                {
                    document = p;
                }
            }

            if (document == null)
            {
                DateTime now = DateTime.Now;
                document = new PhoneAction
                {
                    id = d.OrgID,
                    tpcode = d.TPCode,
                    created = now,
                    date = now.AddDays(1),
                    changes = now
                };
            }
        }

        void SaveDoc()
        {
            SetDocValue();
            UpdateCollection upd = new UpdateCollection();
            upd.Add(PhoneAction.OBJECT_NAME).Add(document);
            progressLayout.Visibility = System.Windows.Visibility.Visible;

            bool result = Update.WriteObjects(upd, null);

            progressLayout.Visibility = System.Windows.Visibility.Hidden;

            if (result)
            {
                Update.PutStored(document);
                apply = true;
                Close();
            }
            else
                StdDialog.UpdateErrMsg(this);
        }

        private void BtnOK_Click(object sender, RoutedEventArgs e)
        {
            SaveDoc();
        }


        private void SetDocValue()
        {
            if (contacts.SelectedItem is Org.OrgContact c)
            {
                document.contactFIO = c.name;
                document.contactPHONE = c.Phone;
            }

            document.rejectCause = RC;
            document.fio = FIO;
            document.text = Text;
            document.remark = Remark;
        }

        private void BtnClose_Click(object sender, RoutedEventArgs e)
        {
            Close();
        }

        private void BtnOrder_Click(object sender, RoutedEventArgs e)
        {
            SaveDoc();

            // document.created == order.linked
            PriceWindow pw = new PriceWindow(OrgID, document.created.Ticks / 10000, document.date)
            {
                WindowListener = WindowListener,
            };

            pw.Show();
        }

        private void Window_Loaded(object sender, RoutedEventArgs e)
        {
            btnOK.IsEnabled = false;
        }

        private void Window_Closed(object sender, EventArgs e)
        {
            FireWindowClosed();
        }

        private void FireWindowClosed()
        {
            if (WindowListener != null)
                WindowListener.Closed(this, apply);
        }

        private void TextBox_TextChanged(object sender, TextChangedEventArgs e)
        {
            FireDocChanged();
        }

        private void FireDocChanged()
        {
            if (docIninted)
                btnOK.IsEnabled = true;
        }

        private void Contacts_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            FireDocChanged();
        }

        private void ComboBox_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            FireDocChanged();
        }

        private void Open_RejectCauseEditor(object sender, RoutedEventArgs e)
        {
            RejectCauseEditor rce = new RejectCauseEditor();
            rce.WindowListener = this;
            rce.Show();
        }

        void IWindowListener.Closed(Window window, bool apply)
        {
            if (apply && window is RejectCauseEditor e)
            {
                RejectCauseList = Update.GetStoredList<RejectCause>(RejectCause.OBJECT_NAME);
            }
        }
    }
}
