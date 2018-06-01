package integration;

import org.shboland.persistence.db.repo.CarRepository;
import org.shboland.persistence.db.hibernate.bean.Car;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.util.ArrayList;
import org.shboland.domain.entities.JsonPerson;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.http.HttpStatus;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.shboland.api.Application;
import org.shboland.persistence.db.hibernate.bean.Person;
import org.shboland.persistence.db.repo.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashSet;
import java.util.Set;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("test")
public class PersonResourceIT {

    private MockMvc mockMvc;
    
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private PersonRepository personRepository;

    
    @Autowired
    private CarRepository carRepository;

     // @InjectInput

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @After
    public void tearDown() {
        // @TearDownInputTop
      
        carRepository.deleteAll();
        personRepository.deleteAll();
        // @TearDownInputBottom
    }

    // @Input

    @Test
    public void testGetCars_withPersonWithCars() throws Exception {
    
        Person person = IntegrationTestFactory.givenAPersonWithCar(personRepository, carRepository);
            IntegrationTestFactory.givenACar(carRepository);

        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.get("/persons/" + person.getId() + "/cars"))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.OK.value(), response.getStatus());
        assertTrue("Wrong grand total returned.", response.getContentAsString().contains("\"grandTotal\":1"));
        assertTrue("Wrong number of results returned.", response.getContentAsString().contains("\"numberOfResults\":1"));
    }

    @Test
    public void testGetCars_withPersonNoCars() throws Exception {
    
        Person person = IntegrationTestFactory.givenAPerson(personRepository);

        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.get("/persons/" + person.getId() + "/cars"))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.OK.value(), response.getStatus());
        assertTrue("Wrong grand total returned.", response.getContentAsString().contains("\"grandTotal\":0"));
        assertTrue("Wrong number of results returned.", response.getContentAsString().contains("\"numberOfResults\":0"));
        assertTrue("Wrong entities returned.", response.getContentAsString().contains("\"results\":[]"));
    }

    @Test
    public void testGetCars_withoutPerson() throws Exception {
    
        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.get("/persons/-1/cars"))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.OK.value(), response.getStatus());
        assertTrue("Wrong grand total returned.", response.getContentAsString().contains("\"grandTotal\":0"));
        assertTrue("Wrong number of results returned.", response.getContentAsString().contains("\"numberOfResults\":0"));
        assertTrue("Wrong entities returned.", response.getContentAsString().contains("\"results\":[]"));
    }

    @Test
    public void testDeleteCar_withPersonWithCars() throws Exception {
    
        Person person = IntegrationTestFactory.givenAPersonWithCar(personRepository, carRepository);
        Car car = new ArrayList<>(person.getCarSet()).get(0);

        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.delete("/persons/" + person.getId() + "/cars/" + car.getId()))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.OK.value(), response.getStatus());
        assertTrue("Wrong entity returned.", response.getContentAsString().isEmpty());
        assertFalse("Wrong entity link returned.",
                mockMvc.perform(MockMvcRequestBuilders.get("/persons/" + person.getId() + "/cars"))
                        .andReturn().getResponse().getContentAsString()
                        .contains("/cars/" + car.getId()));
    }

    @Test
    public void testDeleteCar_withPersonNoCars() throws Exception {
    
        Person person = IntegrationTestFactory.givenAPerson(personRepository);

        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.delete("/persons/" + person.getId() + "/cars/-1"))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue("Wrong entity returned.", response.getContentAsString().isEmpty());
    }

    @Test
    public void testDeleteCar_withoutPerson() throws Exception {
    
        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.delete("persons/-1/cars/-1"))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue("Wrong entity returned.", response.getContentAsString().isEmpty());
        

        mockMvc.perform(MockMvcRequestBuilders.delete("/persons/-1/cars/-1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    
    @Test
    public void testPutCar_withPerson() throws Exception {

        Person person = IntegrationTestFactory.givenAPerson(personRepository);
        Car car = IntegrationTestFactory.givenACar(carRepository);

        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.put("/persons/" + person.getId() + "/cars/" + car.getId()))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.OK.value(), response.getStatus());
        assertTrue("Wrong entity returned.", response.getContentAsString().isEmpty());
        assertTrue("Wrong entity link returned.",
                mockMvc.perform(MockMvcRequestBuilders.get("/persons/" + person.getId() + "/cars"))
                        .andReturn().getResponse().getContentAsString()
                        .contains("/cars/" + car.getId()));
    }
    
    @Test
    public void testPutCar_withPersonWithCar() throws Exception {
    
        Person person = IntegrationTestFactory.givenAPersonWithCar(personRepository, carRepository);
        Car car = new ArrayList<>(person.getCarSet()).get(0);

        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.put("/persons/" + person.getId() + "/cars/" + car.getId()))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.OK.value(), response.getStatus());
        assertTrue("Wrong entity returned.", response.getContentAsString().isEmpty());
        assertTrue("Wrong entity link returned.",
                mockMvc.perform(MockMvcRequestBuilders.get("/persons/" + person.getId() + "/cars"))
                        .andReturn().getResponse().getContentAsString()
                        .contains("/cars/" + car.getId()));
    }

    @Test
    public void testPutCar_withPersonNoCar() throws Exception {
    
        Person person = IntegrationTestFactory.givenAPerson(personRepository);

        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.put("/persons/" + person.getId() + "/cars/-1"))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue("Wrong entity returned.", response.getContentAsString().isEmpty());
    }

    @Test
    public void testPutCar_withoutPerson() throws Exception {
    
        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.put("/persons/-1/cars/-1"))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue("Wrong entity returned.", response.getContentAsString().isEmpty());
    }

    @Test
    public void testGetPerson_withPerson() throws Exception {

        Person person = IntegrationTestFactory.givenAPerson(personRepository);

        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.get("/persons/" + person.getId()))
                        .andReturn().getResponse();
                        
        assertEquals("Wrong status code returned.", HttpStatus.OK.value(), response.getStatus());
        assertTrue("Wrong entity link returned.", response.getContentAsString().contains("/persons/" + person.getId()));
        assertTrue("Wrong field returned.", response.getContentAsString().contains("\"name\":" + "\"" + person.getName() + "\""));
        // @FieldInputAssert
    }

    @Test
    public void testGetPerson_withoutPerson() throws Exception {
    
        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.get("/persons/-1"))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue("Wrong entity returned.", response.getContentAsString().isEmpty());
    }

    @Test
    public void testList_withoutPersons() throws Exception {
    
        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.get("/persons"))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.OK.value(), response.getStatus());
        assertTrue("Wrong grand total returned.", response.getContentAsString().contains("\"grandTotal\":0"));
        assertTrue("Wrong number of results returned.", response.getContentAsString().contains("\"numberOfResults\":0"));
        assertTrue("Wrong entities returned.", response.getContentAsString().contains("\"results\":[]"));
    }

    @Test
    public void testList_withPersons() throws Exception {
    
        Person savedPerson = IntegrationTestFactory.givenAPerson(personRepository);
        IntegrationTestFactory.givenAPerson(personRepository);

        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.get("/persons"))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.OK.value(), response.getStatus());
        assertTrue("Wrong grand total returned.", response.getContentAsString().contains("\"grandTotal\":2"));
        assertTrue("Wrong number of results returned.", response.getContentAsString().contains("\"numberOfResults\":2"));
        assertTrue("Wrong entity link returned.", response.getContentAsString().contains("persons/" + savedPerson.getId()));
    }

    @Test
    public void testPostPerson_invalidObject() throws Exception {
    
         MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.post("/persons"))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue("Wrong entity returned.", response.getContentAsString().isEmpty());
    }

    @Test
    public void testPostPerson_newObject() throws Exception {
    
        JsonPerson person = IntegrationTestFactory.givenAJsonPerson();

        MockHttpServletResponse response =
                mockMvc.perform(IntegrationTestUtils.doPost("/persons", person))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.CREATED.value(), response.getStatus());
        assertTrue("Wrong entity link returned.", response.getContentAsString().contains("/persons/"));
        assertTrue("Wrong field returned.", response.getContentAsString().contains("\"name\":" + "\"" + person.getName() + "\""));
        // @FieldInputAssert
    }

    @Test
    public void testPutPerson_invalidObject() throws Exception {
    
        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.put("/persons/-1", new Object()))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertTrue("Wrong entity returned.", response.getContentAsString().isEmpty());
    }

    @Test
    public void testPutPerson_newObject() throws Exception {
    
        JsonPerson person = IntegrationTestFactory.givenAJsonPerson();

        MockHttpServletResponse response =
                mockMvc.perform(IntegrationTestUtils.doPut("/persons/-1", person))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.OK.value(), response.getStatus());
        assertTrue("Wrong entity link returned.", response.getContentAsString().contains("/persons/"));
        assertTrue("Wrong field returned.", response.getContentAsString().contains("\"name\":" + "\"" + person.getName() + "\""));
        // @FieldInputAssert
    }

    @Test
    public void testPutPerson_updateObject() throws Exception {
    
        Person savedPerson = IntegrationTestFactory.givenAPerson(personRepository);

        JsonPerson person = IntegrationTestFactory.givenAJsonPerson();

        MockHttpServletResponse response =
                mockMvc.perform(IntegrationTestUtils.doPut("/persons/" + savedPerson.getId(), person))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.OK.value(), response.getStatus());
        assertTrue("Wrong entity link returned.", response.getContentAsString().contains("/persons/"));
        assertTrue("Wrong field returned.", response.getContentAsString().contains("\"name\":" + "\"" + person.getName() + "\""));
        // @FieldInputAssert
    }

    @Test
    public void testDeletePerson_unknownObject() throws Exception {
    
        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.delete("/persons/-1"))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertTrue("Wrong entity returned.", response.getContentAsString().isEmpty());
    }

    @Test
    public void testDeletePerson_deleteObject() throws Exception {
    
        Person savedPerson = IntegrationTestFactory.givenAPerson(personRepository);

        MockHttpServletResponse response =
                mockMvc.perform(MockMvcRequestBuilders.delete("/persons/" + savedPerson.getId()))
                        .andReturn().getResponse();

        assertEquals("Wrong status code returned.", HttpStatus.OK.value(), response.getStatus());
        assertTrue("Wrong entity returned.", response.getContentAsString().isEmpty());
        assertFalse("Entity not deleted", personRepository.findById(savedPerson.getId()).isPresent());
    }

}
