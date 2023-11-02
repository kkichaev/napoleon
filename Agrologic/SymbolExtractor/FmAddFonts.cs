using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Text;
using System.Windows.Forms;
using System.Xml.Serialization;

namespace SymbolsExtractor
{
    public partial class FmAddFonts : Form
    {
        List<FontData> alcGraph = new List<FontData>();

        bool dirty = false;
        string fileName = "";
        public FmAddFonts()
        {
            InitializeComponent();

            dgvFonts.AutoGenerateColumns = false;
            dgvFonts.DataSource = new BindingList<FontData>();

            FontData fd;
            fd = new FontData();
            fd.name = "AlcGraph font1";
            alcGraph.Add(fd);
            fd = new FontData();
            fd.name = "AlcGraph font2";
            alcGraph.Add(fd);
            fd = new FontData();
            fd.name = "AlcGraph font3";
            alcGraph.Add(fd);

            dgvAlcGraph.AutoGenerateColumns = false;
            dgvAlcGraph.DataSource = alcGraph;
        }

        protected override void OnClosing(CancelEventArgs e)
        {
            base.OnClosing(e);
            if (dirty)
            {
                DialogResult res = MessageBox.Show("Last changes not saved. Save changes?", "Warning", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
                if (res == System.Windows.Forms.DialogResult.Cancel)
                {
                    e.Cancel = true;
                    return;
                }
                if (res == System.Windows.Forms.DialogResult.Yes)
                {
                    Save();
                }
            }
        }

        protected override void OnLoad(EventArgs e)
        {
            base.OnLoad(e);
            dirty = false;
        }

        public string FontFolder { get; set; }

        bool AskFileName()
        {
            SaveFileDialog sfd = new SaveFileDialog();
            sfd.Title = "Select file to save data";
            sfd.Filter = "Symbols files (*.smbfnt)|*.smbfnt|All files|*.*";
            if (sfd.ShowDialog() == System.Windows.Forms.DialogResult.Cancel)
                return false;

            fileName = sfd.FileName;
            return true;
        }

        void UpdateTitle()
        {
            string text = (dirty ? "* " : "");

            text += "Additional fonts and symbols";
            if (fileName.Length > 0)
            {
                text += " - " + fileName;
            }

            Text = text;
        }

        private void toolStripButton4_Click(object sender, EventArgs e)
        {
            DialogResult = System.Windows.Forms.DialogResult.Cancel;
            Close();
        }

        private void toolStripButton1_Click(object sender, EventArgs e)
        {
            OpenFileDialog ofd = new OpenFileDialog();
            ofd.Title = "Select file to open";
            ofd.Filter = "Symbols files (*.smbfnt)|*.smbfnt|All files|*.*";
            if (ofd.ShowDialog() == System.Windows.Forms.DialogResult.Cancel)
                return;

            fileName = ofd.FileName;
            Open();
            UpdateTitle();
        }

        private void toolStripButton2_Click(object sender, EventArgs e)
        {
            if (Save())
            {
                DialogResult = System.Windows.Forms.DialogResult.OK;
                Close();
            }
        }

        private void toolStripButton3_Click(object sender, EventArgs e)
        {
            if (AskFileName())
            {
                Save();
                DialogResult = System.Windows.Forms.DialogResult.OK;
                Close();
            }
        }

        private bool Save()
        {
            if (fileName.Length == 0 && !AskFileName())
                return false;

            dgvFonts.CommitEdit(DataGridViewDataErrorContexts.Commit);

            XmlSerializer formatter = new XmlSerializer(typeof(AddFontData));
            using (FileStream fs = new FileStream(fileName, FileMode.Create))
            {
                formatter.Serialize(fs, Data);
            }

            dirty = false;
            return true;
        }

        void Open()
        {
            try
            {
                XmlSerializer formatter = new XmlSerializer(typeof(AddFontData));
                using (FileStream fs = new FileStream(fileName, FileMode.Open))
                {
                    AddFontData c = (AddFontData)formatter.Deserialize(fs);

                    tbCommonSymbols.Text = c.AddSymbols;
                    tbFontSymbols.Text = c.FontSymbols;
                    dgvFonts.DataSource = new BindingList<FontData>(c.Fonts);

                    if(c.alcGraph.Count > 0)
                    {
                        alcGraph.Clear();
                        foreach (FontData fd in c.alcGraph)
                            alcGraph.Add(fd);
                    }

                    dirty = false;
                }

            }
            catch (Exception e)
            {
                MessageBox.Show(e.Message, "Exception while open file " + fileName);
            }
        }

        public AddFontData Data
        {
            get
            {
                AddFontData c = new AddFontData();
                c.AddSymbols = tbCommonSymbols.Text;
                c.FontSymbols = tbFontSymbols.Text;
                c.Fonts.AddRange(((IList<FontData>)dgvFonts.DataSource));
                c.alcGraph = alcGraph;

                return c;
            }

            set
            {
                tbCommonSymbols.Text = value.AddSymbols;
                tbFontSymbols.Text = value.FontSymbols;
                dgvFonts.DataSource = new BindingList<FontData>(value.Fonts);

                if(alcGraph != value.alcGraph)
                {
                    alcGraph.Clear();
                    foreach (FontData fd in value.alcGraph)
                        alcGraph.Add(fd);
                }
            }
        }

        private void tbCommonSymbols_TextChanged(object sender, EventArgs e)
        {
            dirty = true;
            UpdateTitle();
        }

        private void textBox1_TextChanged(object sender, EventArgs e)
        {
            dirty = true;
            UpdateTitle();
        }

        private void dgvFonts_CurrentCellDirtyStateChanged(object sender, EventArgs e)
        {
            string dp = dgvFonts.Columns[dgvFonts.CurrentCell.ColumnIndex].DataPropertyName;
            if (dp == "Italic" || dp == "Bold")
            {
                dgvFonts.CommitEdit(DataGridViewDataErrorContexts.Commit);
            }
        }

        private void dgvFonts_CellValueChanged(object sender, DataGridViewCellEventArgs e)
        {
            dirty = true;
            UpdateTitle();
        }

        private void dgvFonts_CellDoubleClick(object sender, DataGridViewCellEventArgs e)
        {
            if (dgvFonts.Columns[e.ColumnIndex].DataPropertyName == "FontFile")
            {
                assignFont(dgvFonts, e.RowIndex, e.ColumnIndex);
                //FontData fi = dgvFonts.Rows[e.RowIndex].DataBoundItem as FontData;
                //SelectFont sf = new SelectFont();
                //sf.SetFolder(FontFolder);
                //if (sf.ShowDialog() == System.Windows.Forms.DialogResult.OK)
                //{
                //    dgvFonts.Rows[e.RowIndex].Cells[e.ColumnIndex].Value = sf.SelectedFile;
                //}
            }
        }

        private void dgvAlcGraph_CellMouseDoubleClick(object sender, DataGridViewCellMouseEventArgs e)
        {
            if (dgvAlcGraph.Columns[e.ColumnIndex].DataPropertyName == "FontFile")
            {
                assignFont(dgvAlcGraph, e.RowIndex, e.ColumnIndex);
            }
        }

        void assignFont(DataGridView dgv, int row, int column)
        {
            FontData fi = dgv.Rows[row].DataBoundItem as FontData;
            SelectFont sf = new SelectFont();
            sf.SetFolder(FontFolder);
            if (sf.ShowDialog() == System.Windows.Forms.DialogResult.OK)
            {
                dgv.Rows[row].Cells[column].Value = sf.SelectedFile;
            }
        }
    }

    public class AddFontData
    {
        List<FontData> fonts = new List<FontData>();

        internal List<FontData> alcGraph = new List<FontData>();

        public string AddSymbols { get; set; }
        public string FontSymbols { get; set; }

        public List<FontData> Fonts { get { return fonts; } }

        public List<FontData> AlcGraphData { get { return alcGraph; } }
    }
}
