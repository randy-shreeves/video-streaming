import { Routes, Route } from "react-router-dom";
import ProtectedRoute from "./components/ProtectedRoute";
import LoginPage from "./pages/LoginPage";
import RegistrationPage from "./pages/RegistrationPage";
import MovieListPage from "./pages/MovieListPage";
import MovieDetailPage from "./pages/MovieDetailPage";
import WatchPage from "./pages/WatchPage";
import AdminMoviePage from "./pages/AdminMoviePage";
import AddMoviePage from "./pages/AddMoviePage";
import EditMoviePage from "./pages/EditMoviePage";

function App() {
  return (
    <Routes>

      <Route path="/" element={<LoginPage />}/>

      <Route path="/register" element={<RegistrationPage />}/>

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

      <Route
        path="/admin/movies"
        element={
          <ProtectedRoute requiredRole="ROLE_ADMIN">
            <AdminMoviePage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin/movies/new"
        element={
            <ProtectedRoute requiredRole="ROLE_ADMIN">
                <AddMoviePage />
            </ProtectedRoute>
        }
      />

      <Route
        path="admin/movies/:id/edit"
        element={
          <ProtectedRoute requiredRole="ROLE_ADMIN">
            <EditMoviePage />
          </ProtectedRoute>
        }
      />
      
    </Routes>
  );
}

export default App;