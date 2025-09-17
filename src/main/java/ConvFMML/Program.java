/*
 * Copyright © 2017-2019 Rerrah
 *
 * MIDI to MML Converter
 */

package ConvFMML;


class Program {

    /// <summary>
    /// アプリケーションのメイン エントリ ポイントです。
    /// </summary>
    static void Main() {
        try {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new ConvFMML.Form.Form1());
        } catch (Exception ex) {
            MessageBox.Show("不明なエラーが発生しました。\nException Message: " + ex.Message, "エラー", MessageBoxButtons.OK, MessageBoxIcon.Error);
        }
    }
}
