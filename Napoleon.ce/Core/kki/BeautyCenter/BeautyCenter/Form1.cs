using System.Data;
using System.Data.SqlClient;

namespace BeautyCenter
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            new FmAdmin().Show();
        }

      private void Form1_Load(object sender, EventArgs e)
      {
         //string connetionString;
         //SqlConnection cnn;
         //connetionString = @"Data Source=GHOST;Initial Catalog=beautycenter;User ID=sa;Password=1";
         //cnn = new SqlConnection(connetionString);
         //cnn.Open();
         //cnn.Close();

         using (SqlConnection conn = new SqlConnection(Properties.Resources.ConnectString))
         {
            try
            {
               string query = "select name from roles order by id";
               SqlDataAdapter da = new SqlDataAdapter(query, conn);
               conn.Open();
               DataSet ds = new DataSet();
               da.Fill(ds, "roles");
               cbRoles.DisplayMember = "name";
               cbRoles.ValueMember = "name";
               cbRoles.DataSource = ds.Tables["roles"];
            }
            catch (Exception ex)
            {
               // write exception info to log or anything else
               MessageBox.Show("Error occured!");
            }
         }
      }
   }
}