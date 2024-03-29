package com.ipdec.reportsapi.security;

import com.ipdec.reportsapi.api.exceptionhandler.exception.AutenticacaoException;
import com.ipdec.reportsapi.domain.model.Backend;
import com.ipdec.reportsapi.domain.repository.BackendRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {


    @Autowired
    private BackendRepository backendRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        List<String> controllersFilter = new ArrayList<>();
        controllersFilter.add("/jasper");

        String controllerJasper = "/jasper";

        String nome = request.getHeader("user");
        String senha = request.getHeader("password");

        if (uri.startsWith(controllerJasper)) {
            if (nome == null || senha == null) {
                throw new AutenticacaoException("Não autenticado");
            } else {
                Backend backend = backendRepository.findByNome(nome)
                        .orElseThrow(() -> new AutenticacaoException("Não autenticado"));
                if (!new BCryptPasswordEncoder().matches(senha, backend.getSenha())) {
                    throw new AutenticacaoException("Não autenticado");
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }
}
