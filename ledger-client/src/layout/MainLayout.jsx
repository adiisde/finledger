import { Outlet } from "react-router-dom";
import NavBar from "../components/NavBar";

export default function MainLayout() {
  return (
    <div className="flex flex-col min-h-screen w-[80vw] mx-auto">
      <NavBar />
      <main className="flex-1 w-full">
        <Outlet />
      </main>
    </div>
  );
}