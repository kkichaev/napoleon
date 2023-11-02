using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Data.OleDb;
using System.Drawing;
using System.Text;
using System.Threading;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
    public partial class FmPriceLoad : Form
    {
        public FmPriceLoad()
        {
            InitializeComponent();
        }

        private void btnOpen_Click(object sender, EventArgs e)
        {
            if (openFileDialog1.ShowDialog() == DialogResult.OK)
            {
                tbPath.Text = openFileDialog1.FileName;
            }
        }

        private void bntLoad_Click(object sender, EventArgs e)
        {
            Thread thread = new Thread(ExportData);
            thread.Start();
        }

        private void ExportData()
        {
            SimpleDataSet<ManagerFolder> addFolders = new SimpleDataSet<ManagerFolder>("FoldersToWrite", false);
            SimpleDataSet<Price> addPrice = new SimpleDataSet<Price>(Price.WR_OBJECT, false);

            var conStr = string.Format("Provider=Microsoft.ACE.OLEDB.12.0;Data Source={0};Extended Properties='Excel 12.0 Xml;HDR=NO;IMEX=1';", tbPath.Text.Trim());

            var objConn = new OleDbConnection(conStr);
            objConn.Open();
            var dt = objConn.GetOleDbSchemaTable(OleDbSchemaGuid.Tables, null);

            if (dt == null)
            {
                return;
            }

            List<string> loadedFolders = new List<string>();

            foreach (DataRow sh in dt.Rows)
            {
                try
                {
                    string group = sh["TABLE_NAME"].ToString().Trim();
                    var adapter = new System.Data.OleDb.OleDbDataAdapter("SELECT * FROM [" + group + "]", conStr);
                    var ds = new DataSet();
                    adapter.Fill(ds, group);
                    DataTable data = ds.Tables[group];

                    Invoke(new InvokeDelegate(
                      delegate
                      {
                          progressBar1.Value = 0;
                          progressBar1.Maximum = data.Rows.Count;
                      }));

                    foreach (DataRow row in data.Rows)
                    {

                        Invoke(new InvokeDelegate(
                        delegate
                        {
                            progressBar1.PerformStep();
                        }));

                        object[] r = row.ItemArray;
                        if (r.Length < 15) continue;

                        int width, wall;
                        if (!int.TryParse(r[1].ToString(), out width))
                            width = 0;

                        if (!int.TryParse(r[2].ToString(), out wall))
                            wall = 0;

                        Price p = new Price();
                        p.id = r[0].ToString().Trim();
                        p.width = width;
                        p.wall = wall;

                        p.diameter = GetDiameter(r[3].ToString());
                        p.brand = r[4].ToString().Trim();
                        p.subbrand = r[5].ToString().Trim();

                        string folderid = p.brand + "\t" + p.subbrand;
                        p.fid = folderid;
                        p.model = r[6].ToString().Trim();

                        //p.name = String.Format("{0} [{1}*{2}*R{3}]", r[6], width, wall, p.diameter);
                        p.name = String.Format("{0}*{1}*R{2} {3} {4} {5} {6} {7} {8}",  
                          width, wall, p.diameter, 
                          r[4].ToString().Trim(),
                          r[5].ToString().Trim(),
                          r[6].ToString().Trim(),
                          r[7].ToString().Trim(),
                          r[8].ToString().Trim(),
                          r[9].ToString().Trim());

                        p.autoType = r[7].ToString();

                        p.season = GetSeason(r[8].ToString().ToLower());
                        p.studded = GetStudded(r[9].ToString().ToLower());

                        p.keySKU = IsKeySKU(r[10].ToString().ToLower());

                        double.TryParse(r[13].ToString(), out p.cost1);
                        int.TryParse(r[15].ToString(), out p.docFilter);

                        addPrice.Add(p);

                        if (loadedFolders.Contains(p.brand) == false)
                        {
                            loadedFolders.Add(p.brand);

                            ManagerFolder mf = new ManagerFolder();
                            mf.fid = p.brand;
                            mf.name = p.brand;
                            mf.parent = "";
                            addFolders.Add(mf);
                        }

                        if (loadedFolders.Contains(folderid) == false)
                        {
                            loadedFolders.Add(folderid);
                            ManagerFolder mf = new ManagerFolder();
                            mf.fid = folderid;
                            mf.name = p.subbrand;
                            mf.parent = p.brand;
                            addFolders.Add(mf);
                        }
                    }
                }
                catch (Exception e)
                {
                    MessageBox.Show(e.ToString());
                }
            }
            objConn.Close();

            List<ReplacedSet> rpl = new List<ReplacedSet>();
            ReplacedSet rpp = new ReplacedSet(addPrice);
            rpl.Add(rpp);
            if (addFolders.Count > 0)
            {
                ReplacedSet rpf = new ReplacedSet(addFolders);
                rpl.Add(rpf);
            }

            Config cfg = Config.GetConfig();

            if (DataModule.UpdateDataSet(null, null, rpl, cfg.GetConnection()))
            {
                Invoke(new InvokeDelegate(
                delegate
                {
                    MessageBox.Show("Данные по товарам загружены");
                    Close();
                }));
            }
            else
                Invoke(new InvokeDelegate(
                delegate
                {
                    MessageBox.Show("Ошибка записи в базу данных.", "Ошибка", MessageBoxButtons.OK,
                  MessageBoxIcon.Error);
                }));

        }

        private int GetDiameter(string v)
        {
            int ret;
            int.TryParse(v, out ret);
            if(ret == 0)
            {
                string res = "";
                foreach (char c in v.ToCharArray())
                {
                    if (char.IsDigit(c))
                    {
                        res += c;
                    }
                    else
                    {
                        break;
                    }
                }
                int.TryParse(res, out ret);
            }

            return ret;
        }

        private int IsKeySKU(string v)
        {
            return v == "да" || v == "1" ? 1 : 0;
        }

        private int GetSeason(string season)
        {
            return season == "лето" ? 2 : season == "всесезонная" ? 3 : 0;
        }

        private int GetStudded(string studded)
        {
            return studded == "шипованная" ? 1 : studded == "фрикционная" ? 2 : 0;
        }
    }
}