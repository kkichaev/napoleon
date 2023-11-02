using System;
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
using System.Windows.Shapes;

namespace Ads2017
{
    /// <summary>
    /// Логика взаимодействия для SelectDivisionAgents.xaml
    /// </summary>
    public partial class SelectDivisionAgents : Window
    {
        Division division;
        public SelectDivisionAgents()
        {
            InitializeComponent();
        }

        public event AgentSelectedHandler SelectAgents;

        Division FindDivision(System.Collections.IEnumerable dvs, Agent a)
        {
            foreach(Division d in dvs)
            {
                if (d.HaveAgent(a))
                    return d;
            }

            return null;
        }

        public void SetSelectedAgents(Division division, IList<Division.DivisionAgent> agents)
        {
            this.division = division;

            List<string> selected = new List<string>();
            System.Collections.IEnumerable all = Update.GetList(Agent.OBJECT_NAME);
            System.Collections.IEnumerable dvs = Update.GetList(Division.OBJECT_NAME);

            foreach (Division.DivisionAgent da in agents)
                selected.Add(da.id);

            List<Item> items = new List<Item>();
            foreach(Agent a in all)
            {
                Item  it = new Item(a, FindDivision(dvs, a), selected.Contains(a.id));
                items.Add(it);
            }

            items.Sort();
            dgAgents.ItemsSource = items;
        }

        class Item : IComparable<Item>
        {
            public Agent a;
            string division;

            public Item(Agent a, Division d, bool sel)
            {
                this.a = a;
                this.division = d == null ? "" : d.Name;
                IsSelected = sel;
            }

            public bool IsSelected { get; set; }
            public string Name { get => a.Name; }
            public string Division { get => division; }

            public int CompareTo(Item other)
            {
                return Name.CompareTo(other.Name);
            }
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            DialogResult = false;
        }

        private void Button_Click_1(object sender, RoutedEventArgs e)
        {
            if(SelectAgents != null)
            {
                AgentSelectedArgs arg = new AgentSelectedArgs();
                arg.division = division;
                arg.agents = new List<Agent>();

                foreach(Item a in dgAgents.Items)
                {
                    if (a.IsSelected)
                        arg.agents.Add(a.a);
                }

                SelectAgents.Invoke(this, arg);
            }
            DialogResult = true;
        }
    }


    public class AgentSelectedArgs
    {
        public List<Agent> agents;
        public Division division;
    }
    public delegate void AgentSelectedHandler(object sender, AgentSelectedArgs arg);
}
