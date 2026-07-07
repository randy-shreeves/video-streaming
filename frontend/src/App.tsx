import { Routes, Route } from "react-router-dom";
import MovieListPage from "./pages/MovieListPage";
import MovieDetailPage from "./pages/MovieDetailPage";
import WatchPage from "./pages/WatchPage";

function App() {
  return (
    <Routes>
      <Route path="/" element={<MovieListPage />} />
      <Route path="/movies" element={<MovieListPage />} />
      <Route path="/movies/:id" element={<MovieDetailPage />} />
      <Route path="/movies/:id/watch" element={<WatchPage />} />
    </Routes>
  );
}

export default App;