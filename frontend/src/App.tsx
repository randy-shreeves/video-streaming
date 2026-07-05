import { Routes, Route } from "react-router-dom";
import MovieListPage from "./pages/MovieListPage";
import MovieDetailPage from "./pages/MovieDetailPage";

function App() {
  return (
    <Routes>
      <Route path="/" element={<MovieListPage />} />
      <Route path="/movies" element={<MovieListPage />} />
      <Route path="/movies/:id" element={<MovieDetailPage />} />
    </Routes>
  );
}

export default App;