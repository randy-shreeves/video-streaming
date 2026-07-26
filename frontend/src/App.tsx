import { Routes, Route } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import MovieListPage from "./pages/MovieListPage";
import MovieDetailPage from "./pages/MovieDetailPage";
import WatchPage from "./pages/WatchPage";
import ProtectedRoute from "./components/ProtectedRoute";

function App() {
  return (
    <Routes>

      <Route path="/" element={<LoginPage />}/>

      <Route 
        path="/movies" 
        element={
          <ProtectedRoute>
            <MovieListPage />
          </ProtectedRoute>
        }
      />

      <Route 
        path="/movies/:id" 
        element={
          <ProtectedRoute>
            <MovieDetailPage />
          </ProtectedRoute>
        } 
      />

      <Route 
        path="/movies/:id/watch" 
          element={
            <ProtectedRoute>
              <WatchPage />
            </ProtectedRoute>
          } 
        />
    </Routes>
  );
}

export default App;