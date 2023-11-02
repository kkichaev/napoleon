using System.Windows;

namespace Napoleon
{
    public interface IWindowListener
    {
        void Closed(Window window, bool apply);
    }
}
