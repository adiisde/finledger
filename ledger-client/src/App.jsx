import { Route, Routes } from "react-router-dom";
import DocsPage from "./pages/DocsPage";
import TransactionsPage from "./pages/TransactionsPage";
import LedgerEntriesPage from "./pages/LedgerEntriesPage";
import LogsPage from "./pages/LogsPage";
import AccountsPage from "./pages/AccountsPage";
import MainLayout from "./layout/MainLayout";

function App() {
  return (
    <div>
      <Routes>
        <Route path="/" element={<MainLayout />}>
          <Route index element={<DocsPage />} />
          <Route path="accounts" element={<AccountsPage />} />
          <Route path="transactions" element={<TransactionsPage />} />
          <Route path="ledger" element={<LedgerEntriesPage />} />
          <Route path="logs" element={<LogsPage />} />
        </Route>
      </Routes>
    </div>
  );
}

export default App;
