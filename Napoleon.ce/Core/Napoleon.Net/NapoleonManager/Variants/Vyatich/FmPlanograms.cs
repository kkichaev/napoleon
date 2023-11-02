using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
    public partial class FmPlanograms : Form
    {
        SimpleDataSet<Planograms> planograms = new SimpleDataSet<Planograms>(Planograms.OBJECT_NAME, false);
        public FmPlanograms()
        {
            InitializeComponent();

            dgvItems.AutoGenerateColumns = false;
        }

        protected override void OnLoad(EventArgs e)
        {
            base.OnLoad(e);
            LoadData();
        }

        private void LoadData()
        {
            List<IDataSet> upd = new List<IDataSet>();
            upd.Add(planograms);

            FmWait.StdDataRefresh(this, upd, DoLoadData);
        }

        void DoLoadData()
        {
            List<RowData> data = new List<RowData>();
            foreach(Planograms p in planograms.Data)
            {
                data.Add(new RowData(p));
            }

            dgvItems.DataSource = new BindingList<RowData>(data);
        }

        protected override void OnClosing(CancelEventArgs e)
        {
            base.OnClosing(e);
            if (!CheckChanges())
                e.Cancel = true;
        }

        bool CheckChanges()
        {
            if (!btnSave.Enabled)
                return true;

            DialogResult dr = MessageBox.Show("Сохранить изменения?", "Вопрос", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
            if (dr == DialogResult.No)
                return true;
            if (dr == DialogResult.Cancel)
                return false;

            return SaveChanges(false);
        }

        private bool SaveChanges(bool showDialog)
        {
            dgvItems.CommitEdit(DataGridViewDataErrorContexts.Commit);

            List<ReplacedSet> rpl = new List<ReplacedSet>();
            SimpleDataSet<Planograms> wr = new SimpleDataSet<Planograms>(Planograms.OBJECT_NAME, false);
            foreach(RowData rd in ((BindingList<RowData>)dgvItems.DataSource))
            {
                wr.Add(rd.Source);
            }

            bool ret = true;
        
            ReplacedSet rs = new ReplacedSet(wr);
            rpl.Add(rs);
            ret = DataModule.UpdateDataSet(null, null, rpl, Config.GetConfig().GetConnection());

            if (showDialog)
            {
                MessageBox.Show(ret ? "Изменения сохранены" : "Ошибка при записи изменений");
            }
            return ret;
        }

        private void btnRefresh_Click(object sender, EventArgs e)
        {
            LoadData();
        }

        private void btnSave_Click(object sender, EventArgs e)
        {
            btnSave.Enabled = !SaveChanges(true);
        }

        public class RowData
        {
            Planograms src;

            public RowData(Planograms src)
            {
                this.src = src;
            }

            public RowData()
            {
                src = new Planograms();
            }

            public string Name { get { return src.name; } set { src.name = value; } }

            public string FileName { get { return src.path; } set { src.path = value; } }

            public Planograms Source { get { return src; } }

            public Image Photo
            {
                get
                {
                    if (src.photo == null)
                        return null;

                    using (var ms = new MemoryStream(src.photo))
                    {
                        return Image.FromStream(ms);
                    }
                }
                set
                {
                    using (var ms = new MemoryStream())
                    {
                        value.Save(ms, value.RawFormat);
                        src.photo = ms.ToArray();
                    }
                }
            }
        }

        private void btnAdd_Click(object sender, EventArgs e)
        {
            ((BindingList<RowData>)dgvItems.DataSource).AddNew();
            btnSave.Enabled  = true;
        }

        private void btnDelete_Click(object sender, EventArgs e)
        {
            if (dgvItems.CurrentRow != null)
            {
                RowData rd = dgvItems.CurrentRow.DataBoundItem as RowData;
                ((BindingList<RowData>)dgvItems.DataSource).Remove(rd);
                btnSave.Enabled = true;
            }
        }

        private void btnSetPhoto_Click(object sender, EventArgs e)
        {
            if (dgvItems.CurrentRow == null)
                return;

            OpenFileDialog ofd = new OpenFileDialog();
            ofd.Filter = "Все файлы|*.*|Изображения|*.jpg;*.png";
            if(ofd.ShowDialog() == DialogResult.OK)
            {
                try
                {
                    Image i = Image.FromFile(ofd.FileName);
                    RowData rd = dgvItems.CurrentRow.DataBoundItem as RowData;
                    rd.Photo = i;
                    rd.FileName = Path.GetFileName(ofd.FileName);
                    btnSave.Enabled = true;
                } catch(Exception )
                {
                    MessageBox.Show("Ошибка загрузки фото");
                }
            }
        }

        private void dgvItems_CellPainting(object sender, DataGridViewCellPaintingEventArgs e)
        {
            if (e.ColumnIndex == clmnPhoto.Index && e.RowIndex >= 0)
            {
                RowData rd = dgvItems.Rows[e.RowIndex].DataBoundItem as RowData;

                Image img = rd.Photo;
                if (img != null)
                {
                    //e.PaintBackground(e.CellBounds, (e.State & DataGridViewElementStates.Selected) != 0);
                    e.Graphics.FillRectangle(Brushes.White, e.CellBounds);

                    float coefX = (float)e.CellBounds.Width / img.Width;
                    float coefY = (float)e.CellBounds.Height / img.Height;
                    float coef = Math.Min(coefX, coefY);

                    float offsetX = (e.CellBounds.Width - img.Size.Width * coef) / 2;
                    float offsetY = (e.CellBounds.Height - img.Size.Height * coef) / 2;
                    e.Graphics.DrawImage(img, e.CellBounds.Left + offsetX, e.CellBounds.Top + offsetY, img.Size.Width * coef, img.Size.Height * coef);

                    using (Pen linePen = new Pen(SystemBrushes.ControlDark, 1.0f))
                    {
                        //e.Graphics.DrawRectangle(linePen, e.CellBounds);
                        int bottom = e.CellBounds.Bottom - 1;
                        int right = e.CellBounds.Right - 1;
                        e.Graphics.DrawLine(linePen, e.CellBounds.Left, bottom, e.CellBounds.Right, bottom);
                        e.Graphics.DrawLine(linePen, right, e.CellBounds.Top, right, e.CellBounds.Bottom);
                    }
                    e.Handled = true;
                }
            }
        }
    }
}
