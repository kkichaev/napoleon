using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Input;

namespace Ads2017
{
    public class AdsCommands
    {
        private static RoutedUICommand refresh;
        private static RoutedUICommand setting;
        private static RoutedUICommand delete;
        public static RoutedUICommand copy;
        public static RoutedUICommand cut;
        public static RoutedUICommand paste;
        public static RoutedUICommand journal;
        public static RoutedUICommand loadPhoto;
        public static RoutedUICommand agentsInFields;
        public static RoutedUICommand userRoute;
        public static RoutedUICommand distance;
        public static RoutedUICommand userOrder;
        public static RoutedUICommand help;
        public static RoutedUICommand about;
        public static RoutedUICommand users;
        public static RoutedUICommand save;
        public static RoutedUICommand add;
        public static RoutedUICommand open;
        public static RoutedUICommand message;

        static AdsCommands()
        {
            refresh = new RoutedUICommand("Refresh", "Refresh", typeof(AdsCommands), new InputGestureCollection() { new KeyGesture(Key.F5) });
            setting = new RoutedUICommand("Setting", "Setting", typeof(AdsCommands), new InputGestureCollection() { new KeyGesture(Key.Q, ModifierKeys.Control) });
            delete = new RoutedUICommand("Delete", "Delete", typeof(AdsCommands), new InputGestureCollection() { new KeyGesture(Key.D, ModifierKeys.Control) });
            copy = new RoutedUICommand("Copy", "Copy", typeof(AdsCommands), new InputGestureCollection() { new KeyGesture(Key.C, ModifierKeys.Control) });
            cut = new RoutedUICommand("Cut", "Cut", typeof(AdsCommands), new InputGestureCollection() { new KeyGesture(Key.X, ModifierKeys.Control) });
            paste = new RoutedUICommand("Paste", "Paste", typeof(AdsCommands), new InputGestureCollection() { new KeyGesture(Key.V, ModifierKeys.Control) });
            journal = new RoutedUICommand("Journal", "Journal", typeof(AdsCommands), new InputGestureCollection() { new KeyGesture(Key.J, ModifierKeys.Control) });
            loadPhoto = new RoutedUICommand("LoadPhoto", "LoadPhoto", typeof(AdsCommands), new InputGestureCollection() { new KeyGesture(Key.F5, ModifierKeys.Control) });
            agentsInFields = new RoutedUICommand("AgentsInFields", "AgentsInFields", typeof(AdsCommands), new InputGestureCollection() { new KeyGesture(Key.A, ModifierKeys.Control) });
            userRoute = new RoutedUICommand("UserRoute", "UserRoute", typeof(AdsCommands), new InputGestureCollection() { new KeyGesture(Key.R, ModifierKeys.Control) });
            distance = new RoutedUICommand("Distance", "Distance", typeof(AdsCommands), new InputGestureCollection() { new KeyGesture(Key.K, ModifierKeys.Control) });
            userOrder = new RoutedUICommand("UserOrder", "UserOrder", typeof(AdsCommands), new InputGestureCollection() { new KeyGesture(Key.U, ModifierKeys.Control) });
            help = new RoutedUICommand("Help", "Help", typeof(AdsCommands), new InputGestureCollection() { new KeyGesture(Key.F1) });
            about = new RoutedUICommand("About", "About", typeof(AdsCommands), new InputGestureCollection() { new KeyGesture(Key.O, ModifierKeys.Control) });
            users = new RoutedUICommand("Users", "Users", typeof(AdsCommands), new InputGestureCollection() { new KeyGesture(Key.L, ModifierKeys.Control) });
            save = new RoutedUICommand("Save", "Save", typeof(AdsCommands), new InputGestureCollection() { new KeyGesture(Key.S, ModifierKeys.Control) });
            add = new RoutedUICommand("Add", "Add", typeof(AdsCommands), new InputGestureCollection() { new KeyGesture(Key.F8) });
            open = new RoutedUICommand("Open", "Open", typeof(AdsCommands), new InputGestureCollection() { new KeyGesture(Key.O, ModifierKeys.Control) });
            message = new RoutedUICommand("Message", "Message", typeof(AdsCommands), new InputGestureCollection() { new KeyGesture(Key.F6) });
        }

        public static RoutedUICommand Refresh
        {
            get { return refresh; }
        }

        public static RoutedUICommand Setting
        {
            get { return setting; }
        }

        public static RoutedUICommand Delete
        {
            get { return delete; }
        }

        public static RoutedUICommand Copy
        {
            get { return copy; }
        }

        public static RoutedUICommand Cut
        {
            get { return cut; }
        }

        public static RoutedUICommand Paste
        {
            get { return paste; }
        }

        public static RoutedUICommand Journal
        {
            get { return journal; }
        }

        public static RoutedUICommand LoadPhoto
        {
            get { return loadPhoto; }
        }

        public static RoutedUICommand AgentsInFields
        {
            get { return agentsInFields; }
        }

        public static RoutedUICommand UserRoute
        {
            get { return userRoute; }
        }

        public static RoutedUICommand Distance
        {
            get { return distance; }
        }

        public static RoutedUICommand UserOrder
        {
            get { return userOrder; }
        }

        public static RoutedUICommand Help
        {
            get { return help; }
        }

        public static RoutedUICommand About
        {
            get { return about; }
        }

        public static RoutedUICommand Users
        {
            get { return users; }
        }

        public static RoutedUICommand Save
        {
            get { return save; }
        }

        public static RoutedUICommand Add
        {
            get { return add; }
        }

        public static RoutedUICommand Open
        {
            get { return open; }
        }

        public static RoutedUICommand Message
        {
            get { return message; }
        }
    }
}
