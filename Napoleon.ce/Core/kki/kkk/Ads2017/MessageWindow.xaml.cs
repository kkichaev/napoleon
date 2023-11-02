using System;
using System.Collections.Generic;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;

namespace Ads2017
{
    public partial class MessageWindow : Window
    {
        private const int TEXT_LIMIT = 1000;

        public MessageWindow()
        {
            InitializeComponent();
        }

        public static DependencyProperty RecipientProperty = DependencyProperty.Register("Recipient",
            typeof(string), typeof(MessageWindow), new PropertyMetadata(string.Empty));

        public static DependencyProperty HistoryProperty = DependencyProperty.Register("History",
            typeof(string), typeof(MessageWindow), new PropertyMetadata(string.Empty));

        public static DependencyProperty SndMessageProperty = DependencyProperty.Register("SndMessage",
            typeof(string), typeof(MessageWindow), new PropertyMetadata(string.Empty));

        public static DependencyProperty RemainsProperty = DependencyProperty.Register("Remains",
            typeof(int), typeof(MessageWindow), new PropertyMetadata(0));

        public object Target { get; set; }

        private List<string> recipients = new List<string>();

        private void Window_Loaded(object sender, RoutedEventArgs e)
        {
            Remains = TEXT_LIMIT;
            recipients.Clear();

            if (Target is Division d)
            {
                Recipient = d.name;

                foreach (Division.DivisionAgent a in d.GetAllAgents())
                    recipients.Add(a.id);
            }
            else if (Target is Agent a)
            {
                Recipient = a.name;
                recipients.Add(a.id);
            }
            else
                Close();
        }

        public string Recipient
        {
            get { return (string)GetValue(RecipientProperty); }
            set { SetValue(RecipientProperty, value); }
        }

        public string History
        {
            get { return (string)GetValue(HistoryProperty); }
            set { SetValue(HistoryProperty, value); }
        }

        public string SndMessage
        {
            get { return (string)GetValue(SndMessageProperty); }
            set { SetValue(SndMessageProperty, value); }
        }

        public int Remains
        {
            get { return (int)GetValue(RemainsProperty); }
            set { SetValue(RemainsProperty, value); }
        }

        private void TextBox_TextChanged(object sender, TextChangedEventArgs e)
        {
            string s = ((TextBox)sender).Text;
            Remains = TEXT_LIMIT - s.Length;
            SndMessage = s;
        }

        private void SendMessage()
        {
            string msg = SndMessage.Trim();

            if (msg.Length > 0)
            {
                recipients.ForEach((s) => { Update.SendMessage(s, msg); } );
                AddMessageToHistory(msg);
                SndMessage = string.Empty;
            }
        }

        private void TextBox_PreviewKeyDown(object sender, KeyEventArgs e)
        {
            if (Remains == 0)
                e.Handled = true;

            if (e.Key == Key.Enter )
            {
                if (Keyboard.Modifiers == ModifierKeys.Control)
                {
                    TextBox t = (TextBox)sender;
                    t.Text += Environment.NewLine;
                    t.SelectionStart = t.Text.Length - 1;
                    t.SelectionLength = 0;
                }
                else
                {
                    e.Handled = true;
                    SendMessage();
                }
            }
        }

        private void AddMessageToHistory(string msg)
        {
            History = String.Format("{0}{3}{1}{3}{3}{2}", DateTime.Now.ToString(), msg, History, Environment.NewLine);
        }

        private void History_Click(object sender, RoutedEventArgs e)
        {
            MessageHistoryWindow w = new MessageHistoryWindow();

            StringBuilder sb = new StringBuilder();
            recipients.ForEach((s) => {
                if (sb.Length > 0)
                    sb.Append(",");
                sb.Append("'").Append(s).Append("'");
            });

            w.UserIds = sb.ToString();
            w.Show();
        }

        private void Send_Click(object sender, RoutedEventArgs e)
        {
            SendMessage();
        }
    }
}
